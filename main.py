from dbrun import *
from flask import render_template , url_for , redirect , request, session , abort , flash , g , jsonify
from sqlalchemy import exc
import json
# g in flask exists globally
from functools import wraps
from werkzeug.utils import secure_filename
from time import time

app.secret_key = os.urandom(24)
db.create_all()


UPLOAD_FOLDER = './static/Uploaded'
ALLOWED_EXTENSIONS = set(['jpg','png','jpeg'])
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def allowed_file(filename):
	return '.' in filename and filename.rsplit('.',1)[1].lower() in ALLOWED_EXTENSIONS

def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if g.user is None:
        	flash('Login Required!')
        	return redirect(url_for('home'))
        return f(*args, **kwargs)
    return decorated_function


def check_auth(givenemail, password): 
	result = Person.query.filter_by(email=givenemail).first()
	if result is None :
		return (False,False,-1)
	if result.password == password :
		return (True,True,result.id)
	else :
		return (True,False,-1)


@app.route('/')
def home():
	if g.user :
		return redirect(url_for('protected'))
	return render_template('home.html')

# @app.route('/userhome')
# def userhome():
# 	return 'UserHome'

@app.route('/signup' , methods=['POST'])
def handle_signup():
	email = (request.form['email'])
	password = request.form['password']
	name = request.form['name']
	location = request.form['location']
	gender = request.form['gender']
	age = request.form['age']
	recruitment = request.form['recruitment']
	expertise = request.form['expertise']

	newperson = Person(email=email,
						password=password,
						name=name,
						location=location,
						recruitment=recruitment,
						age=age,
						gender=gender,
						expertise=expertise)
	try:
		db.session.add(newperson)
		db.session.commit()
	except exc.SQLAlchemyError :
		flash('Database Faliure. Apologies.')
		return redirect(url_for('home'))

	flash ('Signup Complete '+name.split(' ')[0]+'. You may now Kitch In')
	return redirect(url_for('home'))


@app.route('/authlogin', methods=["GET","POST"])
def handle_login():
	session.pop('user',None)
	givenemail = str(request.form['emaillogin'])
	password = str(request.form['passwordlogin'])
	e,p,i= check_auth(givenemail,password)
	error = 'email' + str(e) + 'and password' + str(p) 
	if e is True and p is True :
		session['user'] = i
		return redirect(url_for('protected'))
		# return 'Logged In !'
	else :
		flash('Invalid Email/Password')
		return redirect(url_for('home'))


@app.route('/dropsession')
@login_required
def dropsession():
	session.pop('user',None)
	flash('Logged Out!')
	return redirect(url_for('home'))

@app.route('/success/<id>')
@login_required
def user_page(id):
	if g.user != int(id) :
		return redirect(url_for('user_page',id=g.user))

	return render_template("after_login/index.html")
	flash('No Session Running!')
	return redirect(url_for('home'))



@app.route('/profile/<id>')
@login_required
def profile_page(id):
	print type(id)," -> ", id
	try:
		id=int(id)
	except:
		pass
	user = Person.query.filter_by(id=id).first()
	print 'user id is ',type(id),id
	cooks=Cooks.query.filter_by(personid=id).all()
	# cooks_data_time=Cooks.query.filter_by(personid=id).order_by(Cooks.time.desc())
	# print cooks_data_time
	print cooks
	cooks_manip=[]

	for i in xrange(len(cooks)):
		if i%2==0:
			cooks_manip.append(list())
		cooks_manip[int(i/2)].append(cooks[i])
	#lets create data set here of history

	print "and Cooks is ",cooks_manip
	return render_template("after_login/profile.html",user=user,cooks=cooks_manip)


@app.route('/protected')
@login_required
def protected():
	return  redirect(url_for('user_page',id=g.user)) 



class Notify():
	def __init__(self,ntype,timestamp,senderid,cookid,cookname,sendername):
		self.type = ntype
		self.timestamp = timestamp
		self.cookid = cookid
		self.senderid = senderid
		self.cookname = cookname
		self.sendername = sendername

@app.route('/notification')
@login_required
def notification():
	i = g.user 

	txt = 'select * from notification where cookid in (select id from cooks where personid = '+str(i)+') order by timestamp desc'
	mynotifs = db.engine.execute(txt)
	notif = []
	for n in mynotifs :
		print n.ntype , n.timestamp , n.sender , n.cookid,
		cook = Cooks.query.filter_by(id=n.cookid).first()
		print cook.name,
		send_er = Person.query.filter_by(id=n.sender).first()
		print send_er.name
		nntype = n.ntype
		ntimestamp = n.timestamp
		nsendername = send_er.name
		nsenderid = n.sender
		ncook = cook.name
		n = Notify(nntype,ntimestamp,nsenderid,n.cookid,ncook,nsendername)
		notif.append(n)

	return render_template('after_login/notification.html',notif = notif , l = len(notif))


@app.route('/protected-recipe-add')
@login_required
def recipe_page():
	return render_template("after_login/AddRecipe.html")


@app.route('/recipe/<id>')
@login_required
def recipe(id):
	cook = Cooks.query.filter_by(id=id).first()
	person = Person.query.filter_by(id=cook.personid).first()
	return render_template('after_login/recipe.html', cook = cook , person = person)


# @app.route('/trending')
# @login_required
# def trending():
# 	trends = Cooks.query.order_by(Cooks.star.desc()).limit(6)


@app.route('/protected-stage-add' , methods=['GET','POST'])
@login_required
def stages_page():
	if request.method == 'POST' :
		# ['cost', 'description', 'preptime', 'region', 'public', 'ctype', 'name']
		name = request.form['name']
		ctype = request.form['ctype']
		public = request.form['public']
		region = request.form['region']
		description = request.form['description']
		cost = int(request.form['cost'])
		preptime = int(request.form['preptime'])
		image = '/static/Uploaded/default.jpg'
		personid = g.user
		imdef = True
		if 'file' not in request.files :
			print 'Image File not of Correct extention'

		file = request.files['file']
		if file.filename == '':
			pass

		elif file and allowed_file(file.filename):
			originalname = secure_filename(file.filename)
			originalname = originalname.split('.')[-1]
			filename = str(personid) +'_'+(str(time())).replace('.','_') + '.' + originalname
			filepath = os.path.join(app.config['UPLOAD_FOLDER'],filename)
			image = str(filepath)[1:]
			imdef = False

		c = Cooks(name=name,preptime=preptime,ctype=ctype,cost=cost,region=region,public=public,personid=personid,image=image,description=description)
		
		try:
			if imdef is False :
				file.save(filepath)
				print 'Saved@',filepath
			db.session.add(c)
			db.session.commit()	
		except:
			print 'error db-commit / img-save.'
			return render_template('404.html' , errortext='DB ERROR',errornumber='db400'), 400

	return render_template("after_login/CommitIndex.html")





@app.before_request
def before_request():
	g.user = None
	if 'user' in session :
		g.user = session['user']

@app.errorhandler(404)
def page_not_found(e):
    return render_template('404.html' , errortext='Dish Not Found',errornumber='404'), 404


@app.after_request
def add_header(response):   
	response.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate'
	response.headers['Pragma'] = 'no-cache'
	response.headers['X-UA-Compatible'] = 'IE=Edge,chrome=1'
	if ('Cache-Control' not in response.headers):
		response.headers['Cache-Control'] = 'public, max-age=600'
	return response





# -------------------------  API  -------------------------

# @api.route('/')

# 192.168.43.210:5000/api-signup
#					 /apiauthlogin


def request_dict(request):
	r = request.values.to_dict().keys()[0]
	answer = request.values.to_dict()[r]
	r = json.loads(answer)
	print 'Data',r
	return r


@app.route('/archit')
def archit():
	return 'Archit.'

@app.route('/api-signup' , methods=['POST'])
def api_handle_signup():
	r = request_dict(request)
	print 'SIGNUP'
	email = r['email']
	password = r['password']
	name = r['name']
	location = r['location']
	gender = r['gender']
	age = r['age']
	recruitment = r['recruitment']
	expertise = r['expertise']
	data = {'status':'1'}

	newperson = Person(email=email,
						password=password,
						name=name,
						location=location,
						recruitment=recruitment,
						age=age,
						gender=gender,
						expertise=expertise)
	try:
		db.session.add(newperson)
		db.session.commit()
	except exc.SQLAlchemyError :
		data['status'] = '0'
		resp = jsonify(data)
		resp.status_code = 400
		return resp

	resp = jsonify(data)
	resp.status_code = 200
	return resp


@app.route('/apiauthlogin', methods=["POST"])
def api_handle_login():
	print 'LOGIN'
	r = request_dict(request)
	givenemail = str(r['emaillogin'])
	password = str(r['passwordlogin'])
	data = {
		'id':'-1',
		'status':'0',
		'error':''
	}

	e,p,i= check_auth(givenemail,password)
	error = 'email' + str(e) + 'and password' + str(p) 
	data['error'] = error

	if e is True and p is True :
		data['id'] = i
		data['status'] = 1
		resp = jsonify(data)
		resp.status_code = 200
		return resp
	resp = jsonify(data)
	resp.status_code = 400
	return resp





@app.route('/api-notification',methods=["POST"])
def api_notification():

	r = request_dict(request)
	print 'VAlues',request.values
	print 'json' , request.json
	print 'data' , request.data
	i = int(r['id'])
	
	a = []
	final = {'notifications':[]}
	txt = 'select * from notification where cookid in (select id from cooks where personid = '+str(i)+') order by timestamp desc'
	mynotifs = db.engine.execute(txt)

	for n in mynotifs :
		print n.ntype , n.timestamp , n.sender , n.cookid,
		cook = Cooks.query.filter_by(id=n.cookid).first()
		print cook.name,
		send_er = Person.query.filter_by(id=n.sender).first()
		print send_er.name
		nntype = n.ntype
		ntimestamp = n.timestamp
		nsendername = send_er.name
		nsenderid = n.sender
		ncook = cook.name
# Pulled ncook by sendername on timestamp
		current = {'type':nntype,
					'timestamp':ntimestamp,
					'cook':ncook,
					'sendername':nsendername,
					'senderid':nsenderid}

		a.append(current)
	final['notifications']=a
	resp = jsonify(final)
	resp.status_code = 200
	return resp


@app.route('/api-trending' , methods = ['GET'])
def api_trending():
	# image , name , description , cookid 
	image = 'http://192.168.43.210:5000'+url_for('static',filename='Uploaded/default.jpg')
	name = 'Recipe'
	description = 'dsaljf'
	cookid = 0
	print image,name,description,cookid

	trends = Cooks.query.order_by(Cooks.star.desc()).limit(6)
	a = []
	final = {'trending':[]}

	for t in trends :
		# print 'IMAGE:',t.image
		current = {'image':"http://192.168.43.210:5000"+str(t.image),
					'name':t.name,
					'description':t.description,
					'cookid':t.id}
		a.append(current)

	final['trending'] = a

	resp = jsonify(final)
	resp.status_code = 200
	return resp


@app.route('/api-cook-each' , methods=['POST'])
def api_cook_each():
	r = request_dict(request)
	i = int(r['id'])
	final = {	'status':'0',
				'name':'',
				'preptime':'',
				'ctype':'',
				'cost':'',
				'region':'',
				'public':'',
				'image':'',
				'description':'',
				'star':'',
				'personid':''}

	c = Cooks.query.filter_by(id=i).first()
	if c is None :
		resp = jsonify(final)
		resp.status_code = 400
		return resp

	final = {	'status':'1',
				'name':c.name,
				'preptime':c.preptime,
				'ctype':c.ctype,
				'cost':c.cost,
				'region':c.region,
				'public':c.public,
				'image':'http://192.168.43.210:5000'+str(c.image),
				'description':c.description,
				'star':c.star,
				'personid':c.personid}

	resp = jsonify(final)
	resp.status_code = 200
	return resp


@app.route('/api-profile-each' , methods=['POST'])
def api_profile_page():
	r = request_dict(request)
	i = int(r['id']) 



	print 'USER ID',i
	cooks=Cooks.query.filter_by(personid=i).all()

	a = []
	final = {'cooks':[]}
	if cooks is None :
		print 'CURRENT:',None
		current = {	'status':'0',
					'name':'',
					'image':'',
					'description':'',
					'cookid':''}
		a.append(current)
		final['cooks']= a
		resp = jsonify(final)
		resp.status_code = 400
		return resp

	for cook in cooks :
		current = {	'status':'1',
					'name':cook.name,
					'image':"http://192.168.43.210:5000"+str(cook.image),
					'description':cook.description,
					'cookid':cook.id}

		print 'CURRENT:',current
		a.append(current)

	final['cooks']= a
	resp = jsonify(final)
	resp.status_code = 200
	return resp



if __name__ == '__main__':
	app.run(host="0.0.0.0",debug=True)
