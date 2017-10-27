from dbrun import *
from flask import render_template,redirect,flash,url_for, request, Response, jsonify , g
import json
import os
from time import time
from werkzeug.utils import secure_filename

db.create_all()


UPLOAD_FOLDER = './static/Uploaded'
ALLOWED_EXTENSIONS = set(['jpg','png','jpeg'])


app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

pid = 1


@app.route('/apti')
def apti():
	return 'Apti'


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.',1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['POST','GET'])
def upload_file():
	if request.method == 'POST':
		print request.form.keys()
		print 'Text Entered:',str(request.form['tf'])
		print 'Option Selected:',str(request.form['ch'])

		if 'file' not in request.files :
			flash('No file part')

		file = request.files['file']
		if file.filename == '':
			flash('No selected file.')
			return redirect(request.url)

		if file and allowed_file(file.filename):
			originalname = secure_filename(file.filename)
			originalname = originalname.split('.')[-1]
			filename = str(pid) +'_'+(str(time())).replace('.','_') + '.' + originalname
			filepath = os.path.join(app.config['UPLOAD_FOLDER'],filename)
			print 'Saved@',filepath
			name = str(request.form['tf'])
			image = str(filepath)
			public = str(request.form['ch'])
			c = Cooks(name=name,image=image,public=public)

			try:
				file.save(filepath)
				db.session.add(c)
				db.session.commit()
			except:
				print 'error db-commit / img-save.'
				return 'Error'
			return 'Done'

	return render_template('Upload.html')


@app.route('/img')
def show_img():
	pid=1
	user = Cooks.query.filter_by(id=pid).first()
	print user.image
	return render_template('Uploaded.html',user=user)





# @app.route('/uploads/<filename>')
# def uploaded_file(filename):
# 	return render_template('Uploaded.html',filename=filename)

# @app.route('/guess/<filename>')
# def guess(filename):
# 	return recognize(filename)
	

if __name__ == '__main__' :
	app.run(host='0.0.0.0',debug=True)











