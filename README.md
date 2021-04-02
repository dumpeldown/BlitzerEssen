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

## Image & Configuration files
See the [uploaded files](https://github.com/dumpeldown/BlitzerEssen/tree/main/src/main/resources/de/dumpeldown/blitzer/ocr) for reference.
### Image
- The image to be processed must be located unter the resources folder, with the correct package 
  name
- See [tess4j supported files](https://github.com/nguyenq/tess4j#features)
  
### Image Configuration
- for the OCR to properly work you need to add a '.prop' - File in the same directory
- the name must be `<imageName>.prop`
- contains three values: (See example below)
     - days: Number of days displayed in the image (usually 5 or 7)
     - x: pixel value of the border gap from the left side to the first streetname
     - y: pixel value of the border gap from the **top** side to the first streetname
````bash
x = 8 ; y = 40 ; days = 7
````

## Build & run
currently no information, this will be added when the application reaches a more desirable state