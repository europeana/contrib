# MediaChecker

MediaChecker was developed as a collection of static functions that wrap around some of the best media analysis libraries available today. These libraries include [ImageMagick](http://www.imagemagick.org/) for images, [FFMPEG](https://www.ffmpeg.org/) for audio/video and [iTextPDF](http://itextpdf.com/) for PDF files.

## General Purpose

```java
public static String getMimeType(String filename) throws IOException, FileNotFoundException
```

This is the core function, since it returns the mime-type of any file. This should be used in a `switch` block, to redirect the files to the appropriate analysis function.

```java
public static long getFileSize(String filename) throws IOException, FileNotFoundException
```

A helper function that returns the size of a file (regardless of type) in bytes.

```java
public static String getVersion()
```

Another helper function that returns the version of MediaChecker. This should be saved along with the extracted metadata; this will allow for metadata to be updated when newer versions are released, without having to re-processes everything.

## Images

```java
public static ImageInfo getImageInfo(String filename, String colormap) throws IOException, FileNotFoundException, InfoException, InterruptedException, IM4JavaException
```

The function above takes as input the `filename` of the image to be processed, and the filename of a `colormap` to be used for reducing the number of colors in the image, and for selecting the 6 most common colors.  A sample colormap (based on [X11 Colors](http://www.w3.org/TR/css3-color/#svg-color)) can be found in the resources directory (named `colormap.png`).

Processing an image through this function results to an `ImageInfo` object with the following properties:

```java
public int getWidth()
public int getHeight()
```

This functions return the dimensions of the image in pixels (by returning Width and Height respectively).

```java
public String getMimeType()
public String getFileFormat()
```

The first function returns the MIME type of the image, while the second returns the file format of the image (the complete list of file types identified, can be found on [ImageMagick website](http://www.imagemagick.org/script/formats.php?ImageMagick=83iavs40u5qr472mba5qbrst51)).

```java
public String getColorSpace()
```

This function returns `sRGB` for color images and `grayscale` for grayscale images.

```java
public String[] getPalette()
```

Finally, this function returns an array with upto 6 colors (in the HEX code used for web applications). To achieve that, the image is dithered using a pre-defined color map and the 6 most common colors in the image (based on the number of pixels of each color) are selected.

## Video Files

```java
public static VideoInfo getVideoInfo(String filename) throws IOException, FileNotFoundException, IllegalArgumentException
```

This function takes as input the `filename` of the video to be processed, examines the first (video) stream, and returns an object of the `VideoInfo` class with the properties below. Keep in mind that if multiple video streams are present in the container, only the first one is examined.

```java
public int getWidth()
public int getHeight()
public String getResolution()
```

These functions return the dimensions of the video in pixels (by returning Width, Height and a concatenation of both, respectively).

```java
public String getMimeType()
public String getCodec()
```

The first function returns the MIME type of the video, while the second returns the Codec the video is encoded with.

```java
public long getDuration()
public double getFrameRate()
public int getBitRate()
```

Finally, these three functions return the duration of the video (in milliseconds), the frame-rate (in frames per second) and the bit-rate (in bits per second), respectivelly.

## Audio Files

```java
public static AudioInfo getAudioInfo(String filename) throws IOException, FileNotFoundException, IllegalArgumentException
```

This function takes as input the `filename` of the audio file to be processed, examines the first (audio) stream and returns an object of the `AudioInfo` class with the properties below. Keep in mind that if multiple audio streams are present in the container, only the first one is examined.

```java
public String getMimeType()
public String getFileFormat()
```

The first function returns the MIME type of the audio, while the second returns the Codec the audio is encoded with.

```java
public int getSampleRate()
public int getBitRate()
public int getBitDepth()
public int getChannels()
```

Functions to get the quality of the audio file: Sample Rate, Bit Rate, Bit Depth and number of Channels, respectively.  Keep in mind that FLAC files return a bit-rate of 0 since they are loselessly compressed.

```java
public long getDuration()
```

Finally, this function returns the duration of the audio file (in milliseconds).

## Text Files

For text files, the available information is minimal.

```java
protected static boolean isSearchable(String filename) throws IOException, FileNotFoundException
```

This function can be used on any text file (txt, epub, pdf, xml, rtf). With the exeption of PDF, the rest are by definition searchable, and the function returns true.  For PDF files, we check if the file contains only images (returns `false`) or any text (returns `true`). Keep in mind that a PDF file that contains images and even a single character (in the form of plain text) is marked as searchable.

```java
protected static int getDPI(String filename) throws IOException, FileNotFoundException
```

With this function, the user can obtain the DPI of a PDF file, **IF** and **ONLY IF** the file contains a *single image*. If the file contains more than one image, then -1 is returned, while no image results to a returned value of 0.

