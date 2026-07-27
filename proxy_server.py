#!/usr/bin/env python3
"""
Simple middleman proxy for the AJ Arabic Reader Android app.

Why this exists: Android 2.3's SSL/TLS stack is too old to negotiate a
handshake with modern HTTPS servers (no SNI, no TLS 1.2). This tiny
server does the HTTPS fetch itself (using your Mac's modern TLS stack)
and re-serves the raw RSS XML over plain HTTP, which Gingerbread has no
trouble with at all.

Usage:
    python3 proxy_server.py
Then, on the same WiFi network as your phone, find your Mac's local IP
(System Settings > Network, or run `ipconfig getifaddr en0` in Terminal)
and point the app at:
    http://<your-mac-ip>:8080/feed

Requires only the Python standard library - no installs needed.
"""

import urllib.request
from http.server import BaseHTTPRequestHandler, HTTPServer

# The real Al Jazeera Arabic RSS feed. If this 404s, check aljazeera.net's
# page source for the current <link rel="alternate" type="application/rss+xml"> tag.
UPSTREAM_FEED_URL = (
    "https://www.aljazeera.net/aljazeerarss/"
    "a7c186be-1baa-4bd4-9d80-a84941d5f350/"
    "73d0e1b4-532f-45ef-b135-bfdff8b8cab9"
)

PORT = 8080


class ProxyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/feed":
            self.send_response(404)
            self.end_headers()
            return

        try:
            req = urllib.request.Request(
                UPSTREAM_FEED_URL,
                headers={"User-Agent": "Mozilla/5.0 (Macintosh)"},
            )
            with urllib.request.urlopen(req, timeout=15) as upstream:
                data = upstream.read()

            self.send_response(200)
            self.send_header("Content-Type", "application/rss+xml; charset=utf-8")
            self.send_header("Content-Length", str(len(data)))
            self.end_headers()
            self.wfile.write(data)

        except Exception as e:
            self.send_response(502)
            self.send_header("Content-Type", "text/plain; charset=utf-8")
            self.end_headers()
            self.wfile.write(("Upstream fetch failed: %s" % e).encode("utf-8"))

    def log_message(self, format, *args):
        print("[proxy]", args)


if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", PORT), ProxyHandler)
    print("Proxy running on http://0.0.0.0:%d/feed" % PORT)
    print("Point your phone (same WiFi) at http://<this-mac-ip>:%d/feed" % PORT)
    server.serve_forever()
