# Read quality

We assume *no* indels. The error distribution for a read of length $N$ with quality scores $q_1, \ldots , q_N$ is then a [Poisson binomial distribution][poisson-binomial-distribution], with parameters

$$
  p_i = 10^\frac{-q_i}{10}
$$

we will denote this distribution by $PB_Q$ where $Q = \{p_i\}_{i: I}$.

## Expected number of errors

For some reason, in [Edgar 2015][edgar2015error], we see a rather convoluted derivation of the expectation of $PB_Q$; as $PB_Q = ∑_{p: Q} B_p$ we know that

$$
  E[PB_Q] = ∑_{p:Q} E[B_p] = ∑ p_i
$$

For reference, its variance is $σ^2 = ∑(1 -p_i)p_i$.

## Le Cam theorem

We have an interesting inequality due to Le Cam, which connects $PB_Q$ with a Poisson distribution $λ = E[PB_Q] = ∑p_i$.

#### Le Cam inequality

$PB_Q$ and $P_λ$ with $λ = E[PB_Q]$ are connected by

$$
  \sum_{k = 0}^\infty \| PB_Q(k) - P_λ(k) \| \le 2 \sum_Q p_i^2
$$

This approximation will be then good if

1. $Q$ is big
2. The $p_i$s are small

For example, a read with length $150$ and all quality scores over $35$ the bound above (recall that this is *total* variation distance) will be at most $0.00003$.

## References

- [Error filtering, pair assembly and error correction for next-generation sequencing reads][edgar2015error]
- ...

<!-- refs -->
[poisson-binomial-distribution]: https://en.wikipedia.org/wiki/Poisson_binomial_distribution
[edgar2015error]: https://academic.oup.com/bioinformatics/article/31/21/3476/194979/Error-filtering-pair-assembly-and-error-correction
[testing-poisson-binomial-distributions]: https://people.csail.mit.edu/jayadev/papers/soda2014pbd.pdf
