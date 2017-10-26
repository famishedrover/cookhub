from dbrun import *
from flask import render_template,redirect,flash,,url_for, request, Response, jsonify
import json
import os
from werkzeug.utils import secure_filename

db.create_all()


UPLOAD_FOLDER = './Uploaded'
ALLOWED_EXTENSIONS = set(['jpg','png','jpeg'])


app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER



def allowed_file(filename):
	return '.' in filename and filename.rsplit('.',1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['POST','GET'])
def upload_file():
	if request.method == 'POST':
		print 'Text Entered:',str(request.form['tf'])

		if 'file' not in request.files :
			flash('No file part')

		file = request.files['file']
		if file.filename == '':
			flash('No selected file.')
			return redirect(request.url)

		if file and allowed_file(file.filename):
			filename = secure_filename(file.filename)
			filepath = os.path.join(app.config['UPLOAD_FOLDER'],filename)
			print 'Saved@',filepath
			name = str(request.form['tf'])
			image = str(filepath)
			c = Cooks(name=name,image=image)

			try:
				file.save(filepath)
				db.session.add(c)
				db.session.commit()
			except:
				print 'error db-commit / img-save.'
				return 'Error'

			return 'Done'

	return render_template('Upload.html')

# @app.route('/uploads/<filename>')
# def uploaded_file(filename):
# 	return render_template('Uploaded.html',filename=filename)

# @app.route('/guess/<filename>')
# def guess(filename):
# 	return recognize(filename)
	

if __name__ == '__main__' :
	app.run(host='0.0.0.0',debug=True)











