This project was build in order to parse the XML Human Metaboline 
Database. It utilizes the SAX java parer library, and a custom handler
made for this purpose. The project consists of three main packages.
The core package containing the main method, and the custom handler
the Exporters package with a class responsible for writing to file
and lastly, the Preprocessing package that houses a simple method
that normalizes some input.

This code is written to be easy to modify in order to parse complex
and large in size, XML databases. Given an XML file input (hardcoded),
it returns a .tsv file. If one wants to extract a .csv file instead,
one must alter the "/t" char added in the end of every string in the 
characters() method inside the handler. As well as the makeFile() method
inside the ExoprtToFile class.

On the other hand, if someone wants to parse another XML db, one
must know its structure and also pass this structure to the lookup 
hashmap through the DocumentPreProcessor class. Along with the location
of the XML in the main method as well as the name of the exported file
inside the HMDBHandler.

<b><u> How the parsing is achieved: </u></b>

The SAX parser reads the whole document in one pass, it does not
stop till it reaches the end. This process has the benefit of <b>not 
being memory demanding</b>, so it is preferred for larger files.
On the flipside this approach is agnostic of the document's structure.
So the programmer must incorporate mechanisms that understands 
where the data the parser is reading belong to. In this occasion
a heap is used in order for the handler to know which tag element
am I parsing and its parents. I compare this tag from the heap along with its parents, 
with the structure of the XML file, that I keep in a separate
array to find the position of the data I am currently parsing. That position
indicates the cell on the HashMap where I should store the parsed data.
As we know the XMl structure is repeating multiple times until the end
of the file. That means that the XML db has a lot of entries. Another way
my implementation of the SAX parser is memory efficient has to do with the
writing to file buffer. When a complete entry of the XML is parsed, it is immediately written to the disk.
The challenge here is the varying vertical length of each element in an entry.
Some fields(elements) of the XML are lists while others are
simple values or maybe multiple lines of text. This is solved by reading 
the buffer <i>(consisting of multiple arrays that each correspond 
to an element of an XML entry)</i> horizontally! Meaning, reading
the first element of each array at a time, till the last array. 
In case a cell is empty, and some of its neighbours are not I fill
with tabs, so the entry won't be polluted by the next entry trying 
to fill spaces that must be empty.

George Kafadaris.