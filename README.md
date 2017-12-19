# wiki-geosearch

This sample project performs geosearch via wiki.

First, it obtains current devices location. On failure it uses some random location in Europe.<br/>
Then, it performs geosearch with wiki to get articles around obtained location.<br/>
Based on these articles app fetches list of associated images. Note that the resulting data might be paginated.<br/>
Finally, resulting data is populated into RecyclerView, each row can be expanded to show corresponding list of images.

Application runs under Android 21+.

## These awesome libraries were used:
* [Dagger 2](https://google.github.io/dagger) to decompose application.
* [OkHttp 3](https://github.com/square/okhttp) and [Retrofit 2](https://github.com/square/retrofit) for simple and convinient networking.
* [RxJava 2](https://github.com/ReactiveX/RxJava) and [RxBinding](https://github.com/JakeWharton/RxBinding). Well, you know for what.
* [smart-location-lib](https://github.com/mrmans0n/smart-location-lib) by [Nacho Lopez](https://github.com/mrmans0n) for location helper with Rx adapter.

## References:
* How to perform [geo search](https://www.mediawiki.org/wiki/API:Showing_nearby_wiki_information);
* How to [get images](https://www.mediawiki.org/wiki/API:Images);
* [Working with paginated data](https://www.mediawiki.org/wiki/API:Raw_query_continue).
