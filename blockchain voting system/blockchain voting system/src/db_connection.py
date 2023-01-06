import pymysql


def selectall(query, value):
    con = pymysql.connect(host='localhost', user='root', password='', port=3306, db='voting')
    cmd = con.cursor()
    cmd.execute(query, value)
    result = cmd.fetchall()
    return result


def selectall2(query):
    con = pymysql.connect(host='localhost', user='root', password='', port=3306, db='voting')
    cmd = con.cursor()
    cmd.execute(query)
    result = cmd.fetchall()
    return result


def selectone(query, value):
    con = pymysql.connect(host='localhost', user='root', password='', port=3306, db='voting')
    cmd = con.cursor()
    cmd.execute(query, value)
    result = cmd.fetchone()
    return result


def iud(query, value):
    con = pymysql.connect(host='localhost', user='root', password='', port=3306, db='voting')
    cmd = con.cursor()
    cmd.execute(query, value)
    result = cmd.lastrowid
    con.commit()
    return result


def androidselectall(q, val):
    con = pymysql.connect(host='localhost', port=3306, user='root', passwd='', db='voting')
    cmd = con.cursor()
    cmd.execute(q, val)
    s = cmd.fetchall()
    row_headers = [x[0] for x in cmd.description]
    json_data = []
    print(json_data)
    for result in s:
        json_data.append(dict(zip(row_headers, result)))
    return json_data


def androidselectallnew(q):
    con = pymysql.connect(host='localhost', port=3306, user='root', passwd='', db='voting')
    cmd = con.cursor()
    cmd.execute(q)
    s = cmd.fetchall()
    row_headers = [x[0] for x in cmd.description]
    json_data = []
    print(json_data)
    for result in s:
        json_data.append(dict(zip(row_headers, result)))
    return json_data
