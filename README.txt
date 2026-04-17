README — Huffman Coding (PA9)

Benchmark Results


Calgary Corpus
file                                original    compressed    time
bib                                   111261        73795    0.088s
book1                                 768771       439409    0.466s
book2                                 610856       369335    0.384s
geo                                   102400        73592    0.077s
news                                  377109       247428    0.254s
obj1                                   21504        17085    0.020s
obj2                                  246814       195131    0.214s
paper1                                 53161        34371    0.039s
paper2                                 82199        48649    0.051s
paper3                                 46526        28309    0.031s
paper4                                 13286         8894    0.011s
paper5                                 11954         8465    0.010s
paper6                                 38105        25057    0.027s
pic                                   513216       107586    0.112s
progc                                  39611        26948    0.033s
progl                                  71646        44017    0.046s
progp                                  49379        31248    0.034s
trans                                  93695        66252    0.070s

total bytes read:         3,251,493
total compressed bytes:   1,845,571
total percent compression: 43.239%
compression time:         1.967s


BooksAndHTML
file                                original    compressed    time
A7_Recursion.html                      41163        26189    0.029s
CiaFactBook2000.txt                  3497369      2260664    2.329s
ThroughTheLookingGlass.txt            188199       110293    0.117s
jnglb10.txt                           292059       168618    0.174s
kjv10.txt                            4345020      2489768    2.563s
melville.txt                           82140        47364    0.051s
quotes.htm                             61563        38423    0.041s
rawMovieGross.txt                     117272        53833    0.058s
revDictionary.txt                    1130523       611618    0.630s
syllabus.htm                           33273        21342    0.024s

total bytes read:         9,788,581
total compressed bytes:   5,828,112
total percent compression: 40.460%
compression time:         6.016s


Waterloo (TIFF images)
file                                original    compressed    time
clegg.tif                            2149096      2034595    2.066s
frymire.tif                          3706306      2188593    2.262s
lena.tif                              786568       766146    0.778s
monarch.tif                          1179784      1109973    1.126s
peppers.tif                           786568       756968    0.769s
sail.tif                             1179784      1085501    1.105s
serrano.tif                          1498414      1127645    1.141s
tulips.tif                           1179784      1135861    1.146s

total bytes read:        12,466,304
total compressed bytes:  10,205,282
total percent compression: 18.137%
compression time:        10.393s


Analysis Questions

1. What kinds of files lead to lots of compression? Why?

   Text-heavy files compressed the best (Calgary around 43%, BooksAndHTML
   around 40%). Plain text only uses a small slice of the 256-byte alphabet,
   and within that slice characters like space and 'e' appear far more often
   than others. Huffman gives common bytes short codes and rare ones long
   codes, which drops the average bits-per-byte well below 8.


2. What kinds of files saw little to no compression? Why?

   The TIFF images in Waterloo only compressed about 18%, with several barely
   shrinking. Image data tends to use most of the 256 byte values with
   roughly similar frequencies, so Huffman has nothing to exploit and every
   byte ends up with a code close to 8 bits.


3. What happens when you compress a .hf file that has already been compressed?

   Almost no savings, and several files actually grew on the second pass (for
   example A7_Recursion.html.hf gained 151 bytes and quotes.htm.hf gained 613
   bytes). The first pass already flattened the byte distribution, so the
   second pass produces near-8-bit codes anyway, and the fixed 8 KB SCF
   header on top of that outweighs any tiny gain.
