"""
Hosted version of the AJ Arabic Reader proxy, for PythonAnywhere.

Why this exists: Android 2.3's TLS stack can't negotiate HTTPS with
modern servers like aljazeera.net. This app fetches the real feed over
HTTPS (using the host's modern TLS stack) and re-serves the raw XML over
plain HTTP, which Gingerbread has no trouble with.

Setup on PythonAnywhere (free tier):
1. Sign up at pythonanywhere.com (free account)
2. Dashboard -> "Web" tab -> "Add a new web app" -> Flask -> your Python 3 version
3. It creates a starter file, usually at:
       /home/<your-username>/mysite/flask_app.py
   Open it in their in-browser editor and replace its contents with
   this file's contents.
4. On the "Web" tab, make sure the "Force HTTPS" slider stays OFF
   (it's off by default) - this is what lets old Android reach it over
   plain HTTP.
5. Click the green "Reload" button on the Web tab.
6. Test it by visiting http://<your-username>.pythonanywhere.com/feed
   in any browser - you should see raw RSS XML.
7. In the Android app, set RssFetcher.FEED_URL to that same URL.

No local PC needs to stay running - PythonAnywhere keeps this alive.
"""

import urllib.request
from flask import Flask, Response

app = Flask(__name__)

# The real Al Jazeera Arabic RSS feed. Al Jazeera occasionally rotates the
# exact feed ID - if requests start failing, check aljazeera.net's page
# source for a <link rel="alternate" type="application/rss+xml"> tag.
UPSTREAM_FEED_URL = (
    "https://www.aljazeera.net/aljazeerarss/"
    "a7c186be-1baa-4bd4-9d80-a84941d5f350/"
    "73d0e1b4-532f-45ef-b135-bfdff8b8cab9"
)


@app.route("/feed")
def feed():
    try:
        req = urllib.request.Request(
            UPSTREAM_FEED_URL,
            headers={"User-Agent": "Mozilla/5.0 (compatible; AJReaderProxy/1.0)"},
        )
        with urllib.request.urlopen(req, timeout=15) as upstream:
            data = upstream.read()
        return Response(data, mimetype="application/rss+xml")
    except Exception as e:
        return Response("Upstream fetch failed: %s" % e, status=502, mimetype="text/plain")


@app.route("/")
def index():
    return "AJ Arabic Reader proxy is running. Feed at /feed"


# PythonAnywhere's WSGI config looks for a variable named `application`.
application = app

if __name__ == "__main__":
    # For local testing only (python flask_app.py). On PythonAnywhere,
    # the platform runs this via WSGI and this block is never executed.
    app.run(host="0.0.0.0", port=8080)
