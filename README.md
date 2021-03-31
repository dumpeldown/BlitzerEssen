# BlitzerEssen
Display a simple map of mobile speedtraps in the city of Essen, Germany.

![Map display all the BLITZERS](https://i.imgur.com/ALXhQhB.png)

## Workflow
1. Get recent data from the twitter account of the [City of Essen](https://twitter.com/Essen_Ruhr)
2. Analyse metadata about the uploaded photo (right now: each week, should be automatized later on)
3. Run OCR over the image using [tess4j](https://github.com/nguyenq/tess4j)
4. Run a _forward_ geocoding query on the street names to retrieve the lat/long data using 
   [locationiq](https://locationiq.com/)
5. Display the location points (and/or location lines in case of long streets) on a map using 
   [JXMapViewer2](https://github.com/msteiger/jxmapviewer2)
   
## Work in Progress
- work with more data to find errors and problems
- add labels in the markers on the map to display the streetname
- for longer streets, use the boundarybox data to display a line through the street (?)
- differentiating between days (monday through sunday) when displaying radar traps
- rewrite serialized data when working with new data (or maybe serialize to multiple files?)

## Build & run
currently no information, this will be added when the application reaches a more desirable state