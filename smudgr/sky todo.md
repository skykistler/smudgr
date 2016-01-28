sky's todo
========
By Saturday:
+ Midi tempo sync
+ Faster/stable spectral shift
+ Test/improve live marbeler
+ Dea&Saint content
  + Hook - chakras bottom to top - hook - chakras top to bottom - hook
  + First and second hook:
    + general images with rainbow spectral shift, maybe video or gif if it's smooth
    + switch between downsampling and converge shift tunnel
    + Tap to luma sort, to sync with tempo
  + Chakras bottom to top:
    + Switch between colored source images
    + Slow wavy marbeler
    + Steady luma sort if fast enough
  + Chakras top to bottom:
    + Separate set of source images
    + Steadily increase energy
  + Last hook and outro
    + Wavy, slow but to the beat
    + Spectral shift
    + Wistful/nostalgic vibes somehow
    + Slow desaturate?
  + http://xonecole.com/wp-content/uploads/2015/05/Chakra-flow-Chart.jpg

Sometime soon:
+ Smooth video input
+ Generalized marbeler using strategy for different interpolation functions, different coord functions
+ gif output
+ Function parameter to change coord/univariate functions

+ Math functions to add:
  + Log
  + Various noise

+ Interpolation functions:
  + Nearest Neighbor
  + Linear
  + Cosine
  + Cubic
  
  
Model
-------
 + Use processing video library
   + With proper frame rate
 + GIFs need proper frame rate
 + GIF output
 
 
View
------
+ Implement CEF
+ Design UI


Controller
----------
+ Sync with MIDI tempo


Smudge
------
+ Implement OpenCL
