3DTools
====

This is a collection of general tools for use with Java3d. 

The most interesting part is probably:
A pure java loader for DDS (DXT) format image files into a java BufferedImage it is tools.ddstexture.utils.DDSDecompressor
It's fast and efficinet, but doesn't beat native GPU compressed dds image usage, as the bufferedimages are 4x as big.


The rest is probably of little use outside my various other projects, as it is not well documented.

Amoungst manay mnay utilities theres a java3d swing overlay system under tools3d.mixed2d3d

And various Camera systems for Java3d (head Trailer, birdseye) under tools3d.camera
