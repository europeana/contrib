#!/usr/bin/env python

import os
import sys
import time
import re
import webbrowser
import threading
import shutil

import lxml.html
import lxml.etree
from flask import Flask, g, request, redirect, url_for, render_template, session, make_response

SERVER_NAME = 'localhost:5010'
app = Flask(__name__)
app.config.update(globals())


def highlight_tree(node, words):
    for child in node:
        highlight_tree(child, words)

    if node.text is not None:
        tokens = re.split(r'(\W+)', node.text)
        node.text = ''
        last = lxml.etree.SubElement(node, 'span')
        for token in tokens:
            if token.lower() in words:
                last = lxml.etree.SubElement(node, 'span')
                last.set('style', 'color: #C00; font-weight: bold')
                last.text = token
            else:
                if last.tail is None: last.tail = ''
                last.tail += token


@app.route('/europeana')
def europeana():
    url = request.args['url']
    hl = request.args.get('hl', '')
    words = re.findall(r'\w+', hl.lower())
    html = lxml.html.parse(url).getroot()
    html.make_links_absolute()
    highlight_tree(html, words)
    return lxml.html.tostring(html)


def need_assessment(line):
    return line.count('\t') == 3


def valid_line(line):
    return line.count('\t') in [3, 4]


def read_assessments(filename):
    with open(filename) as fin:
        return [l.rstrip() for l in fin]


@app.route('/assessment/<int:line>', methods=['POST', 'GET'])
def assessment(line):
    lines = read_assessments(ass_filename)

    if request.method == 'POST':
        rating_line = line
        rating = int(request.form['rating'])

        tmp_filename = ass_filename + '.tmp'
        with open(tmp_filename, 'w') as fout:
            for line_num, line in enumerate(lines):
                if line_num == rating_line:
                    assert valid_line(line)
                    fields = line.split('\t')
                    if len(fields) == 4:
                        fields.append(None)
                    fields[4] = str(rating)
                    line = '\t'.join(fields)
                fout.write(line)
                fout.write('\n')

        shutil.move(tmp_filename, ass_filename)

        return redirect('/')
    else:
        if not valid_line(lines[line]):
            return 'Invalid line'

        fields = lines[line].split('\t')

        query_id, query, europeana_url, old_ass = fields[:4]
        cur_ass = fields[4] if len(fields) == 5 else ''
        return render_template('assessment.html', **locals())


@app.route('/')
def index():
    lines = read_assessments(ass_filename)
    first_line = None
    for line_num, l in enumerate(lines):

        if first_line is None and valid_line(l):
            first_line = line_num

        if need_assessment(l):
            break

    return redirect(url_for('assessment', line=line_num))


@app.route('/skip/<int:line>/<d>')
def skip(line, d):
    d = int(d)
    lines = read_assessments(ass_filename)
    direction = 1 if d > 0 else -1
    last_valid = line
    while d:
        line += direction
        if not 0 <= line < len(lines):
            line = last_valid
            break

        if len(lines[line].split('\t')) in [4, 5]:
            last_valid = line
            d -= direction

    return redirect(url_for('assessment', line=line))


def open_browser(wait_time=0):
    time.sleep(wait_time)
    index_url = 'http://%s/' % SERVER_NAME
    webbrowser.open(index_url)


if __name__ == '__main__':
    flags = []
    positional = []

    for arg in sys.argv[1:]:
        if arg.startswith('--'):
            flags.append(arg)
        else:
            positional.append(arg)

    if '--debug' in flags:
        app.config.update(dict(DEBUG=True))
    else:
        # XXX ugly hack, apparently there is no way to know when Flask is ready
        # to serve requests
        threading.Thread(target=lambda: open_browser(wait_time=0.5)).start()

    if len(positional) != 1:
        print >> sys.stderr, 'Usage: %s <assignments file>' % sys.argv[0]
        sys.exit(1)

    ass_filename, = positional
    app.run()
