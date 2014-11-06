NetProbe
=====

Contains scala implementation of netprobe as specified in the paper below. Constructs a bipartite graph of sellers and their buyers. The paper then uses belief propagation (guilt by association) to iteratively determine the likelihood of a seller being a fraudster, an accomplice or an honest seller.

The implementation, as with the paper, assumes a propagation matrix. The matrix is depicts the behaviour of how the 3 types of sellers interact in eBay marketplace.

We currently do not use any priors for each seller (which we should). Next steps


http://repository.cmu.edu/cgi/viewcontent.cgi?article=1530&context=compsci
