import cmd
import os

import Crypto
import Crypto.Random
import binascii
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA
from collections import OrderedDict
from Crypto.Signature import PKCS1_v1_5

from flask import *
# from jaseem import a
from werkzeug.utils import secure_filename

path = r".\static\application"
app = Flask(__name__)
from src.db_connection import *


class Transaction:
    def __init__(self, sender_address, sender_private_key, recipient_address, value):
        self.sender_address = sender_address
        self.sender_private_key = sender_private_key
        self.recipient_address = recipient_address
        self.value = value

    def __getattr__(self, attr):
        return self.data[attr]

    def to_dict(self):
        return OrderedDict({'sender_address': self.sender_address,
                            'recipient_address': self.recipient_address,
                            'value': self.value
                            })

    def sign_transaction(self):
        """
        Sign transaction with private key
        """
        private_key = RSA.importKey(binascii.unhexlify(self.sender_private_key))
        signer = PKCS1_v1_5.new(private_key)
        h = SHA.new(str(self.to_dict()).encode('utf8'))
        return binascii.hexlify(signer.sign(h)).decode('ascii')


@app.route("/logincode", methods=['post'])
def lgincode():
    uname = request.form['uname']
    password = request.form['password']
    q = "select * from login where username=%s and password=%s AND user_type='student'"
    val = uname, password
    res = selectone(q, val)
    print(res)
    if res is None:
        return jsonify({'task': 'invalid'})
    else:
        return jsonify({'task': 'success', 'lid': res[0], 'type': res[3]})


@app.route("/application", methods=['GET', 'POST'])
def application():
    try:
        studid = request.form['sid']
        print(studid)
        post = request.form['pid']
        applicationform = request.files['files']
        print(applicationform)
        files = secure_filename(applicationform.filename)
        applicationform.save(os.path.join(path, files))
        qry = ("insert into application values(Null,%s,curdate(),curtime(),%s,%s,'pending')")
        val = (str(studid), post, files)
        iud(qry, val)
        return jsonify({'task': 'sucess'})
    except Exception as e:
        return jsonify({'task': 'already added'})


@app.route("/viewpost", methods=['GET', 'POST'])
def viewpost():
    qry = ("select * from post")
    res = androidselectallnew(qry)
    print(res)
    return jsonify(res)


@app.route("/status", methods=['GET', 'POST'])
def status():
    studid = request.form['uid']
    qry = (
        "SELECT post.post_name,application.status FROM application JOIN post ON application.post_id=post.post_id WHERE application.student_id=%s")
    val = studid
    res = androidselectall(qry, val)
    return jsonify(res)


@app.route("/post", methods=['POST'])
def post():
    userid = request.form['lid']
    qry = ("SELECT * FROM `post` WHERE `post_id` NOT IN(SELECT `pid` FROM `voting_tb` WHERE `uid`=%s)")
    qry = ("SELECT * FROM `post`")
    val = userid
    res = androidselectallnew(qry)
    print(res)
    # res=androidselectall(qry,val)
    return jsonify(res)


@app.route("/candidates", methods=['POST'])
def candidates():
    print(request.form)
    post = request.form['post']
    qry = (
        "SELECT `student`.* ,`post`.`post_id`,`post_name`,`student`.`login_id` FROM `student` JOIN `application` ON `application`.`student_id`=`student`.`login_id` JOIN `post` ON `post`.`post_id`=`application`.`post_id` WHERE `application`.`post_id`=%s  AND  `application`.`status`='verified'")
    val = post
    res = androidselectall(qry, val)
    print(res)
    return jsonify(res)


@app.route("/result", methods=['post'])
def result():
    pid = request.form['pid']
    print(pid, "=============================================")
    qry = "SELECT student.`student_name`,COUNT(*)`vote` FROM `student` JOIN `application` ON `application`.student_id=`student`.`login_id`  JOIN  `voteinfo` ON `voteinfo`.`candid`=`student`.`login_id` JOIN `result publish` ON `result publish`.`postid`=`voteinfo`.`post_id`  WHERE `application`.`status`='verified'  AND `result publish`.`status`='published'   AND `application`.`post_id`=`voteinfo`.`post_id` and `voteinfo`.`post_id`=%s GROUP BY `voteinfo`.`candid` ORDER BY vote DESC"
    val = pid
    res = androidselectall(qry, val)
    print(res, "=======================")
    return jsonify(res)


@app.route("/sdsa", methods=['post'])
def sdsa():
    post = request.form['post']
    userid = request.form['lid']
    candid = request.form['cid']
    qry = ("SELECT * FROM `voteinfo` WHERE `post_id`=%s AND `uid`=%s")
    val = post, userid
    vv = selectone(qry, val)
    if vv is None:
        qry = ("insert into voteinfo values(null,%s,%s,1,%s)")
        val = (post, userid, candid)
        iud(qry, val)
        generate1(userid)
        qry = "SELECT * FROM `cipher` WHERE id=%s"
        val = userid
        rse = selectone(qry, val)
        sender_address = rse[1]
        sender_private_key = rse[2]
        transaction = Transaction(sender_address, sender_private_key, str(post), str(candid))
        # response = {'transaction': transaction.to_dict()}
        d = transaction.to_dict()
        d['signature'] = transaction.sign_transaction()
        print(d)
        import requests
        re = requests.post("http://127.0.0.1:8080/transactions/new", data=d)
        print(re.status_code)

        return jsonify({'task': 'Success'})
    else:
        return jsonify({'task': 'faild'})


@app.route('/votingg', methods=['post'])
def votingg():
    return "okok"


@app.route('/generate', methods=['POST'])
def generate():
    id = request.form['studid']
    # random_gen = Crypto.Random.new().read
    # private_key = RSA.generate(1024, random_gen)
    # public_key = private_key.publickey()
    # response = {
    #     'private_key': binascii.hexlify(private_key.exportKey(format='DER')).decode('ascii'),
    #     'public_key': binascii.hexlify(public_key.exportKey(format='DER')).decode('ascii'),
    #
    # }
    qry = ("SELECT public_key,private_key FROM cipher where id=%s")
    val = id
    a = selectone(qry, val)
    if a is None:
        qry = ("insert into cipher values(%s,%s,%s")
        # val=id,str(response['public_key']),str( response['private_key'])
        iud = (qry, val)

    else:
        qry = ("update cipher set public_key=%s,private_key=%s where  id=%s")
        # val= str(response['public_key']),str(response['private_key']),id
        iud = (qry, val)

    return jsonify({'task': "success"})


def generate1(id):
    random_gen = Crypto.Random.new().read
    private_key = RSA.generate(1024, random_gen)
    public_key = private_key.publickey()
    response = {
        'private_key': binascii.hexlify(private_key.exportKey(format='DER')).decode('ascii'),
        'public_key': binascii.hexlify(public_key.exportKey(format='DER')).decode('ascii'),

    }
    qry = ("SELECT public_key,private_key FROM cipher where id=%s")
    val = (id,)
    a = selectone(qry, val)
    if a is None:
        qry = "insert into cipher values(%s,%s,%s)"
        val = (id, str(response['public_key']), str(
            response['private_key']))
        iud(qry, val)


@app.route('/sendcomp', methods=['post'])
def sendcomp():
    pid = request.form['lid']
    comp = request.form['comp']
    qry = "insert into complaint values(NULL,%s,curdate(),%s,'pending')"
    val = (pid, comp)
    iud(qry, val)
    return jsonify({'task': 'successful'})


@app.route('/viewreply', methods=['post'])
def viewreply():
    pid = request.form['lid']
    qry = "select*from complaint where  u_id=%s"
    res = androidselectall(qry, pid)
    return jsonify(res)


@app.route('/viewguide', methods=['post'])
def viewguide():
    qry = "select*from guideline"
    res = androidselectallnew(qry)
    return jsonify(res)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
