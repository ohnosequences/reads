I want to look at the expected error probability across the set of reads. For each read I have


$$
  E: R \to [0,∞[
$$

Now $F(e)$ for $e: ]0,∞[$ will be the proportion of reads with $E(r) ≤ e$. It would be nice to plot this, as it yields a good picture; at least with intervals. How?

1. build a *sorted* map `error -> readCount`
2. $F(x) =$ filter keys under $x$ and sum values

This map could be `ArrayMap[Float,Int]`. That's all we need. It would be pretty small I think.

## Quantiles

Given the data structure above, we can get an iterator on the map (key,value)s (which will be *sorted* by error) and traverse it until we get the corresponding fraction.

## Radix trees

We could store read sequences as a radix tree, with quality/id as their value? we can filter by prefix (adapters), barcodes, etc.
