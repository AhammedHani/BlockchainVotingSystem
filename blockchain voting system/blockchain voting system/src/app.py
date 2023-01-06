from flask import *

app = Flask(__name__)
app.secret_key = "abc"
import pymysql

con = pymysql.connect(host='localhost', port=3306, user='root', passwd='', db='voting')
cmd = con.cursor()


@app.route('/')
def home():
    # return render_template("adminindex1.html")
    return render_template("Login.html")


@app.route('/log', methods=['GET', 'POST'])
def adminlog():
    username = request.form['textfield1']
    password = request.form['textfield2']
    cmd.execute("select * from login where username='" + username + "' and password='" + password + "'")
    log = cmd.fetchone()
    print(log)
    if log == None:
        return '''<script>alert('invalid username or password');window.location='/'</script>'''
    else:

        if log[3] == 'admin':
            session['lid'] = log[0]
            print("ok")
            return '''<script>alert('admin,login successfull');window.location='/adminhome'</script>'''
        if log[3] == 'pollingofficer':
            session['lid'] = log[0]
            print("ok")
            return '''<script>alert('pollingofficer,login successfull');window.location='/pollingofficerhome'</script>'''


@app.route('/adminhome', methods=['GET', 'POST'])
def adminhome():
    return render_template("admin home.html")


@app.route('/verifystud', methods=['GET', 'POST'])
def verifystud():
    cmd.execute(
        "SELECT `application`.`application_id`,`application`.`date`,`student`.*,`post`.`post_name` FROM `student` JOIN `application` ON `application`.`student_id`=`student`.`login_id` JOIN `post` ON `post`.`post_id`=`application`.`post_id` WHERE `application`.`status`='pending'")
    s = cmd.fetchall()
    print(s, "===========")
    return render_template("verify student.html", val=s)


@app.route('/approvestud', methods=['GET', 'POST'])
def approvestud():
    apid = request.args.get('id')
    cmd.execute("update application set status='approved' where application_id='" + apid + "'")
    con.commit()
    return '''<script>alert('approved successfully');window.location='/verifystud'</script>'''


@app.route('/rejectstud', methods=['GET', 'POST'])
def rejectstud():
    apid = request.args.get('id')
    cmd.execute("delete from application where application_id='" + apid + "'")
    con.commit()
    return '''<script>alert('rejected successfully');window.location='/verifystud'</script>'''


@app.route('/addstudent', methods=['GET', 'POST'])
def addstudent():
    return render_template("Student.html")


@app.route('/addstudentt', methods=['GET', 'POST'])
def addstudentt():
    name = request.form['textfield']
    registernumber = request.form['textfield2']
    department = request.form['textfield3']
    sem = request.form['select']
    gender = request.form['radiobutton']
    phonenumber = request.form['textfield4']
    emailid = request.form['textfield5']
    uname = request.form['textfield6']
    pwd = request.form['textfield7']
    cmd.execute("insert into login values(NULL,'" + uname + "','" + pwd + "','student')")
    uid = con.insert_id()
    cmd.execute("insert into student values(NULL,'" + str(
        uid) + "','" + name + "','" + registernumber + "','" + department + "','" + sem + "','" + phonenumber + "','" + emailid + "','" + gender + "')")
    con.commit()
    return '''<script>alert('student registered successfully');window.location='/mngstudent'</script>'''


@app.route('/mngstudent', methods=['GET', 'POST'])
def mngstudent():
    cmd.execute(
        "SELECT `student`.* FROM `student` JOIN `login`  ON `student`.`login_id`=`login`.`login_id` WHERE `login`.`user_type`='student'")
    s = cmd.fetchall()
    return render_template("Manage stud.html", val=s)


@app.route('/addpollingofficer', methods=['GET', 'POST'])
def addpollingofficer():
    return render_template("Poling officer.html")


@app.route('/guide', methods=['GET', 'POST'])
def guide():
    return render_template("guideline.html")


@app.route('/pollingofficer', methods=['GET', 'POST'])
def pollingofficer():
    officername = request.form['textfield']
    phonenumber = request.form['textfield2']
    emailid = request.form['textfield3']
    username = request.form['textfield4']
    password = request.form['textfield5']
    cmd.execute("insert into login values(NULL,'" + username + "','" + password + "','pollingofficer')")
    uid = con.insert_id()
    cmd.execute("insert into pollingofficer values('" + str(
        uid) + "','" + officername + "','" + phonenumber + "','" + emailid + "')")
    con.commit()
    return '''<script>alert('officer registered successfully');window.location='/mangpollingofficer'</script>'''


@app.route('/mangpollingofficer', methods=['GET', 'POST'])
def mangpollingofficer():
    cmd.execute("select * from pollingofficer")

    s = cmd.fetchall()
    return render_template("Managepolling.html", val=s)


@app.route('/managedepartment')
def managedepartment():
    cmd.execute("select * from department")
    res = cmd.fetchall()
    return render_template("manage department.html", v=res)


@app.route('/adddepartment', methods=['post'])
def adddepartment():
    return render_template("add department.html")


@app.route('/verify', methods=['GET', 'POST'])
def verify():
    apid = request.args.get('id')
    cmd.execute("update application set status='verified' where application_id='" + apid + "'")
    con.commit()
    return '''<script>alert('verified successfully');window.location='/viewnominee#about'</script>'''


@app.route('/managecourse')
def managecourse():
    cmd.execute("select * from department")
    res = cmd.fetchall()
    print(res)
    return render_template("manage course.html", val=res)


@app.route('/addcourse', methods=['post'])
def addcourse():
    cmd.execute("select * from department")
    res = cmd.fetchall()
    return render_template("add course.html", v=res)


@app.route('/searchcourse', methods=['post'])
def searchcourse():
    cmd.execute("select * from department")
    res = cmd.fetchall()
    print(res)
    dep = request.form['select']
    print(dep)
    cmd.execute(
        "SELECT `department`.* ,`course`.* FROM department JOIN `course` ON `course`.`deptid`=`department`.`deptid` WHERE `course`.`deptid`='" + dep + "'")
    res1 = cmd.fetchall()
    print(res1)
    return render_template("manage course.html", v=res1, val=res, dep=int(dep))


@app.route('/addc', methods=['post'])
def addc():
    department = request.form['select']
    course = request.form['textfield']
    description = request.form['textarea']
    cmd.execute("insert into course value(NULL,'" + department + "','" + course + "','" + description + "')")

    con.commit()
    return redirect('managecourse')


@app.route('/adddep', methods=['post'])
def adddep():
    department = request.form['textfield']
    description = request.form['textarea']
    cmd.execute("INSERT INTO department VALUES(NULL,'" + department + "','" + description + "')")
    con.commit()
    return redirect('/managedepartment')


@app.route('/delete')
def delete():
    id = request.args.get('id')
    cmd.execute("delete from department where deptid=('" + id + "')")
    con.commit()
    return redirect('/managedepartment')


@app.route('/deletecouse')
def deletecouse():
    id = request.args.get('id')
    cmd.execute("delete from course where courseid=('" + id + "')")
    con.commit()
    return redirect('/managecourse')


@app.route('/result')
def result():
    cmd.execute("select * from post")
    res = cmd.fetchall()
    return render_template("result.html", val=res)


@app.route('/resultsearch', methods=['post'])
def resultsearch():
    post = request.form['select']
    session['pid'] = post
    # qry="SELECT `candidate`.`fname`,`lname`,`party`,COUNT(voteid) vote ,candidate.loginid FROM `candidate` JOIN `vote` ON `vote`.`candid`=`candidate`.`loginid` JOIN `result_publish` ON `result_publish`.`postid`=`vote`.`postid`  WHERE `vote`.`postid`=%s AND `result_publish`.`status`='published'    GROUP BY `vote`.`candid` ORDER BY vote DESC"
    # cmd.execute("SELECT `student`.`student_name`,COUNT(vote) vote ,student.login_id FROM `student` JOIN `voteinfo` ON `voteinfo`.`uid`=`student`.`login_id`   WHERE `voteinfo`.`post_id`='"+ post +"'    GROUP BY `voteinfo`.`uid` ORDER BY vote DESC")
    cmd.execute(
        "SELECT student.`student_name`,COUNT(*)`register_number` FROM `student` JOIN `application` ON `application`.student_id=`student`.`login_id`  JOIN  `voteinfo` ON `voteinfo`.`candid`=`student`.`login_id`   WHERE `application`.`status`='verified'  AND    `voteinfo`.`post_id`='" + post + "' AND `application`.`post_id`=`voteinfo`.`post_id`  GROUP BY `voteinfo`.`candid` ORDER BY vote DESC")
    res = cmd.fetchall()
    cmd.execute("select * from post")
    ressa = cmd.fetchall()
    return render_template("result.html", val1=res, post=str(post), val=ressa)


@app.route('/publishresult', methods=['post'])
def publishresult():
    try:
        pid = session['pid']
        cmd.execute("insert into  `result publish` values(null,'" + pid + "','published')")
        con.commit()
        return '''<script>alert("result publish");window.location="pollingofficerhome"</script>'''

    except Exception as e:
        return '''<script>alert("already publish");window.location="pollingofficerhome"</script>'''


@app.route('/edit')
def edit():
    id = request.args.get('id')
    session['id'] = id
    cmd.execute("select * from department where deptid=('" + id + "')")
    res = cmd.fetchone()
    return render_template("edit department.html", val=res)


@app.route('/editcode', methods=['post'])
def editcode():
    department = request.form['textfield']
    description = request.form['textarea']
    id = session['id']
    cmd.execute(
        "update department set department='" + department + "',description='" + description + "' where deptid='" + id + "' ")
    con.commit
    return redirect('/managedepartment')


@app.route('/editcourse')
def editcourse():
    cmd.execute("select * from department")
    res1 = cmd.fetchall()
    id = request.args.get('id')
    session['id'] = id
    cmd.execute("SELECT * from course where courseid=('" + id + "')")
    res = cmd.fetchone()
    print(res)
    return render_template("edit course.html", v=res1, val=res)


@app.route('/editcoursecode', methods=['post'])
def editcoursecode():
    id = session['id']
    department = request.form['select']
    course = request.form['textfield']
    description = request.form['textarea']
    cmd.execute(
        "UPDATE course SET deptid='" + department + "',course='" + course + "',description='" + description + "' WHERE  courseid='" + id + "' ")
    con.commit
    return redirect('/managecourse')


@app.route('/addguideline', methods=['GET', 'POST'])
def addguideline():
    guideline = request.form['textarea']
    cmd.execute("insert into guideline values(NULL,'" + guideline + "',curdate(),curtime())")
    con.commit()
    return '''<script>alert('guideline successfully added');window.location='/adminhome'</script>'''


@app.route('/viewguideline', methods=['GET', 'POST'])
def viewguideline():
    cmd.execute("select * from guideline")
    # cmd.execute("SELECT `student`.`student_name`,`result`.`number of votes`,`post`.`post_name`,`application`.`time`,`application`.`date`,`application`.`status` FROM `student` JOIN `application` ON `application`.`student_id`=`student`.`login_id` JOIN `result` ON `result`.`student_id`=`application`.`student_id` JOIN `post` ON `post`.`post_id`=`application`.`post_id`")
    s = cmd.fetchall()

    return render_template("view guideline.html", val=s)


@app.route('/deleteguid', methods=['GET', 'POST'])
def deleteguid():
    id = request.args.get('id')
    cmd.execute("DELETE FROM `guideline` WHERE `guideline_id`='" + str(id) + "'")
    con.commit()

    return '''<script>alert(' successfully deleted');window.location='/viewguideline'</script>'''


@app.route('/viewpost', methods=['GET', 'POST'])
def viewpost():
    cmd.execute("select * from post")
    # cmd.execute("SELECT `student`.`student_name`,`result`.`number of votes`,`post`.`post_name`,`application`.`time`,`application`.`date`,`application`.`status` FROM `student` JOIN `application` ON `application`.`student_id`=`student`.`login_id` JOIN `result` ON `result`.`student_id`=`application`.`student_id` JOIN `post` ON `post`.`post_id`=`application`.`post_id`")
    s = cmd.fetchall()

    return render_template("view post.html", val=s)


@app.route('/deletepost', methods=['GET', 'POST'])
def deletepost():
    id = request.args.get('id')
    cmd.execute("DELETE FROM `post` WHERE `post_id`='" + str(id) + "'")
    con.commit()

    return '''<script>alert(' successfully deleted');window.location='/viewpost'</script>'''


@app.route('/ellllldate', methods=['post'])
def ellllldate():
    datenotification = request.form['textarea']
    electiondate = request.form['textfield']
    cmd.execute("insert into `election notification` VALUES (NULL,'" + electiondate + "','" + datenotification + "')")
    con.commit()
    return '''<script>alert("success");window.location='adminhome'</script>'''


@app.route('/electiondate')
def electiondate():
    return render_template("election date.html")


@app.route('/viewnominee')
def viewnominee():
    cmd.execute("SELECT * FROM post")
    res = cmd.fetchall()
    return render_template("view nominee.html", val=res)


@app.route('/searchnomine', methods=['post'])
def searchnomine():
    cmd.execute("SELECT * FROM post")
    res1 = cmd.fetchall()
    post = request.form['select']
    cmd.execute(
        "SELECT `application`.*,`student`.* FROM `application` JOIN `student` ON `application`.`student_id`=`student`.`login_id` WHERE `application`.`post_id`='" + post + "' and application.status='approved'")
    res = cmd.fetchall()
    return render_template("view nominee.html", val1=res, val=res1)


@app.route('/viewresult', methods=['GET', 'POST'])
def viewresult():
    cmd.execute("select * from post")
    res = cmd.fetchall()
    return render_template("view result.html", val=res, pid=0)


@app.route('/voteresult', methods=['GET', 'POST'])
def voteresult():
    pid = request.form['select']
    print(pid)
    cmd.execute("select * from post")
    res = cmd.fetchall()
    cmd.execute(
        "SELECT `student`.`student_name`,COUNT(vote) vote ,student.login_id FROM `student` JOIN `voteinfo` ON `voteinfo`.`candid`=`student`.`login_id`   JOIN `result publish` ON `result publish`.`postid`=`voteinfo`.`post_id`  WHERE `voteinfo`.`post_id`='" + pid + "' AND `result publish`.`status`='published'  GROUP BY `voteinfo`.`candid` ORDER BY vote DESC ")
    s = cmd.fetchall()
    print(s)
    return render_template("view result.html", vals=s, val=res, pid=pid)


@app.route('/viewreport', methods=['GET', 'POST'])
def viewreport():
    cmd.execute(
        "SELECT report.*,pollingofficer.* FROM report JOIN pollingofficer ON pollingofficer.pollingofficer_id=report.pollingofficer_id")
    s = cmd.fetchall()
    return render_template("view report.html", val=s)


@app.route('/pollingofficerhome', methods=['GET', 'POST'])
def pollingofficerhome():
    return render_template("pollingofficer home.html")


@app.route('/addreportissue', methods=['GET', 'POST'])
def addreportissue():
    issue = request.form['textarea']
    a = session['lid']

    cmd.execute("insert into report values(NULL,'" + str(a) + "','" + issue + "',curdate())")
    con.commit()
    return '''<script>alert('report successfully added');window.location='/pollingviewreport'</script>'''


@app.route('/addreportissue1', methods=['GET', 'POST'])
def addreportissue1():
    return render_template("add reportissue.html")


@app.route('/addpost', methods=['GET', 'POST'])
def addpost():
    post_name = request.form['textfield']
    discription = request.form['textarea']
    edate = request.form['text']
    enoti = request.form['textarea1']

    cmd.execute(
        "insert into post values(NULL,'" + post_name + "','" + discription + "','" + edate + "','" + enoti + "')")
    con.commit()
    return '''<script>alert(' successfully added');window.location='/addpost1'</script>'''


@app.route('/addpost1', methods=['GET', 'POST'])
def addpost1():
    return render_template("post.html")


@app.route('/pollingviewreport', methods=['GET', 'POST'])
def pollingviewreport():
    a = session['lid']

    cmd.execute("SELECT * FROM `report` WHERE `pollingofficer_id`= " + str(a))
    s = cmd.fetchall()
    return render_template("pollingview report.html", val=s)


@app.route('/deletereport', methods=['GET', 'POST'])
def deletereport():
    apid = request.args.get('id')
    cmd.execute("DELETE FROM `report` WHERE `report_id`='" + apid + "'")
    con.commit()
    return '''<script>alert('deleted successfully');window.location='/pollingviewreport#about'</script>'''


@app.route('/deletestudent', methods=['GET', 'POST'])
def deletestudent():
    apid = request.args.get('id')
    cmd.execute("DELETE FROM `student` WHERE `student_id`='" + apid + "'")
    con.commit()
    return '''<script>alert('deleted successfully');window.location='/mngstudent#about'</script>'''


@app.route('/deletepollingofficer', methods=['GET', 'POST'])
def deletepollingofficer():
    apid = request.args.get('id')
    cmd.execute("DELETE FROM `login` WHERE `login_id`='" + apid + "'")

    cmd.execute("DELETE FROM `pollingofficer` WHERE `pollingofficer_id`='" + apid + "'")
    con.commit()
    return '''<script>alert('deleted successfully');window.location='/mangpollingofficer'</script>'''


@app.route('/view_complaint')
def view_complaint():
    cmd.execute(
        "SELECT `complaint`.*,`student`.`student_name` FROM `student` JOIN `complaint` ON`complaint`.`u_id`=`student`.`login_id` WHERE `complaint`.`reply`='pending'")
    res = cmd.fetchall()
    print(res)
    return render_template('VIEW_COMPLAINT.html', val=res)


@app.route('/reply')
def reply():
    id = request.args.get('id')
    session['cid'] = id
    return render_template('REPLY.html')


@app.route('/reply1', methods=['post'])
def reply1():
    reply = request.form['textarea']
    cmd.execute("update complaint set reply='" + reply + "' where c_id='" + session['cid'] + "'")
    con.commit()
    return '''<script> alert(" SUCCESSFUL"); window.location = "/view_complaint#about"</script>'''


if __name__ == "__main__":
    app.run(debug=True)
