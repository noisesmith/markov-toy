=======
markov-toy
==========

character markov generator, with arbitrary memory
## Options

`lein run [seed] [memory] [input-files ...]`

Seed will be based on the clock, if the value provided is not parsable as a number. Memory defaults to 1, the input file defaults to the included text of Lincoln's Gettysburg Address. If multiple files are provided, the ngrams are summed from all the inputs. Using the same numeric seed with the same memory and files, will give the same output on each run.

A memory of 4 will typically recreate recognizable English language text.

You can get a decent result with:

`lein run _ 4 resources/little-red.txt resources/gettysburg.txt  resources/gettysburg.txt resources/gettysburg.txt resources/gettysburg.txt`

The multiple instances of a single file raise the relative strengths of ngrams from that file.

## License

Copyright © 2014 Justin Glenn Smith

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

