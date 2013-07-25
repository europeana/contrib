MagickTiler - README
====================
MagickTiler is a utility for converting image files into formats suitable for 
publishing them as zoomable Web images. 

MagickTiler uses GraphicsMagick to perform image manipulation (resizing, cropping,
etc.) Make sure you have GraphicsMagick installed on your system!

http://www.graphicsmagick.org/

Note: MagickTiler should also work with ImageMagick (http://www.imagemagick.org/).
This was not tested yet, however.

Features
--------
- Converts to TMS tiling scheme
- Converts to Pyramid TIFF (PTIF) image format
- Fully embeddable in your own Java app
- Usable as a command-line utility 
- Extensible to other tiling schemes and formats

TODOs
-----
- Check for ImageMagick-compatibility
- Add a simple Swing GUI (drag an image file inside, get thumbnail preview, 
  information about generated tileset - and generate it)
- Support for KML superoverlay and Microsoft Deep Zoom would be nice 