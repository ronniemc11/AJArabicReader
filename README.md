# أخبار الجزيرة — Al Jazeera Arabic RSS Reader (Android 2.3+)

A minimal, real Android app (Java) that fetches and displays live headlines
from Al Jazeera's Arabic RSS feed. Built to run on Android 2.3 Gingerbread
(API level 9) and up — no AndroidX, no modern support libraries, no
dependencies beyond the Android SDK itself.

## What it does
- Fetches Al Jazeera Arabic's RSS feed over HTTP (`RssFetcher` +
  `HttpURLConnection`)
- Parses it with `XmlPullParser` (built into every Android version, no
  external XML library needed)
- Shows a right-aligned, RTL-friendly list of headlines, summaries, and
  relative timestamps ("5 دقيقة", "2 ساعة", ...)
- Tapping a story opens it in an in-app WebView

## Project layout
```
AndroidManifest.xml
res/
  layout/activity_main.xml       - list + loading + error states
  layout/list_item_news.xml      - one headline row
  layout/activity_article.xml    - WebView for full article
  values/strings.xml
  drawable/ic_launcher.xml        - placeholder icon (swap for real artwork)
src/com/example/ajreader/
  MainActivity.java     - loads the feed, drives the list
  ArticleActivity.java  - WebView for tapped stories
  RssFetcher.java        - network fetch
  RssParser.java          - RSS XML parsing
  NewsItem.java, NewsAdapter.java, RelativeTime.java
```

## Building it
Modern Android Studio's default Gradle/AGP versions assume a much higher
`minSdkVersion` than 9, so the smoothest path for genuine Gingerbread
compatibility is one of:

1. **Eclipse + ADT (historically accurate to 2011)** — import this as a
   classic Android project. This folder is already laid out the classic
   way (`AndroidManifest.xml`, `src/`, `res/` at the root), which is what
   old ADT expects.
2. **Android Studio with an old Android Gradle Plugin** (e.g. AGP 1.x/2.x
   with a matching old Gradle version) — you'll need to add a
   `build.gradle` pointing `compileSdkVersion`/`buildToolsVersion` at
   older SDK platform/build-tools packages, which you'd install via the
   SDK Manager's "Show Package Details."
3. **`aapt`/`javac`/`dx` by hand** — fully possible but tedious; only
   worth it if you specifically want zero build tooling.

If you tell me which of these you're set up for, I can generate the
matching `build.gradle` or Eclipse `.project`/`.classpath` files.

## Two proxy options

**Option A — `proxy_server.py`**: runs on your own PC/Mac, only works
while that computer is on and both devices share WiFi. Good for quick
testing, described above.

**Option B — `flask_app.py`**: an always-on hosted version, meant for a
free PythonAnywhere account (or any host that supports Flask/WSGI and
doesn't force HTTPS). Once deployed there, your PC/phone don't need to be
on the same network, and your PC never needs to stay running. See the
docstring at the top of `flask_app.py` for exact setup steps. Once it's
live, point `RssFetcher.FEED_URL` at
`http://<your-username>.pythonanywhere.com/feed` instead of a local IP.

Important: whichever host you use, **make sure it doesn't force HTTPS**
on its default subdomain — the entire point is that the *phone* only ever
speaks plain HTTP; the *host* does the HTTPS fetch to Al Jazeera on your
behalf, using its own modern TLS stack.

## Why there's a proxy at all
Gingerbread's SSL/TLS stack (no SNI, no TLS 1.2) usually **cannot**
complete an HTTPS handshake with aljazeera.net or most modern servers.
Rather than the phone connecting directly, `proxy_server.py` runs on your
Mac (or any always-on machine): it fetches the real feed over HTTPS using
your computer's modern TLS stack, then re-serves the raw XML over plain
HTTP on your local network — which Gingerbread has no trouble with.

To use it:
1. `python3 proxy_server.py` (stdlib only, nothing to install)
2. Find your machine's local IP: `ipconfig getifaddr en0` on a Mac
3. Make sure your phone is on the **same WiFi network**
4. Edit `RssFetcher.FEED_URL` in the source to
   `http://<your-ip>:8080/feed` (a placeholder IP is already there —
   just swap it) and rebuild the app
5. Leave the proxy running on your Mac whenever you want the app to load

This only works while your Mac and phone are on the same network and the
script is running — it's meant for local testing, not a public deployment.

## Important caveats
- **The exact RSS feed URL**: Al Jazeera occasionally rotates the specific
  feed ID used in `proxy_server.py`'s `UPSTREAM_FEED_URL`. If the proxy
  logs a fetch failure, check aljazeera.net's page source for a
  `<link rel="alternate" type="application/rss+xml">` tag and update that
  constant.
- **RTL layout**: True bidi layout mirroring (start/end, auto-mirrored
  drawables) arrived in API 17. On API 9 the app instead right-aligns text
  manually (`android:gravity="right"`), which displays Arabic correctly
  but won't auto-flip other layout elements — normal for a period-accurate
  2.3 app.
- **Launcher icon**: `ic_launcher.xml` is a plain placeholder shape, not
  real artwork or the Al Jazeera logo — swap in your own icon.
- **Content is Al Jazeera's own**: this app only fetches and displays
  their publicly syndicated RSS feed; it doesn't reproduce or repackage
  their branding.
