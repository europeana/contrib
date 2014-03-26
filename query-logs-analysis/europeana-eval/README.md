Simple tool to speed up assessments to Europeana query-click pairs.

If the assessments file is foo.txt, run as:

    ./europeana-eval.py foo.txt

This will open a browser window showing the first document in `foo.txt` that
requires an assessment. By submitting the rating, the browser is redirected to
the next document that needs an assessment. It is possible to move around
assessments with the "Previous" and "Next" links, and it is possible to change
the ratings.

Note that the assessment file is re-read at every request; this is slightly
inefficient but it guarantees the highest consistency.

Requirements: the tool requires Python, lxml and Flask. Both can be installed
with the following command:

    sudo pip install flask lxml


