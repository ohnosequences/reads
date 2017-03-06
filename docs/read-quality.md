# Read quality

We assume *no* indels. The error distribution for a read of length $N$ with quality scores $q_1, \ldots , q_N$ is then a [Poisson binomial distribution][poisson-binomial-distribution], with parameters

$$
  p_i = 10^\frac{-q_i}{10}
$$

we will denote this distribution by $B_Q$ where $Q = \{p_i\}_{i: I}$.

## Expected number of errors

For some reason, in [Edgar 2015][edgar2015error], we see a rather convoluted derivation of the expectation of $B_Q$; as $B_Q = ∑_{p: Q} B_p$ we know that

$$
  E[PB_Q] = ∑_{p:Q} E[B_p] = ∑ p_i
$$

For the same reason, its variance is

$$
  Var(B_Q) = ∑_{p:Q} Var(B_p) = ∑ p_i (1 - p_i)
$$

To give an idea about these numbers, if we have a 150bp read with quality scores above 30, the expected number of errors is bounded above by $0.15$.

## Poisson approximations

### Le Cam theorem

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

As remarked in @hodges1960poisson, we can find a better uniform bound for cumulative distribution functions. Letting

$$
  D = \sup_{n: ℕ} | P_{\mathcal{P}}(n) - P_{B_Q}(n)|
$$

we have that $D ≤ 9 \max{p_i}$. This already makes the approximation incredibly good if the error probabilities are low enough. How low? for example, if all of the scores are over 20, we get $D ≤ 0.09 ≈ 0.1$.

This bound actually applies to *any* subset of $ℕ$.

The interest of this approximation lies in the case where we actually want to compute probabilities for a given $n: ℕ$; do note that if the $p_i$s are small, the variance will be small too, and then Chebyshev will give you that the distribution will be really concentrated near the expected value.

## References

- [Wikipedia - Phred quality score](https://en.wikipedia.org/wiki/Phred_quality_score)
- [Error filtering, pair assembly and error correction for next-generation sequencing reads][edgar2015error]
- [Some elementary results on Poisson approximation in a sequence of Bernoulli trials](https://www.jstor.org/stable/2030354)
- [Le Cam's inequality and Poisson approximations](http://www-stat.wharton.upenn.edu/~steele/Papers/PDF/LIaPA.pdf)
- [SHE-RA shortread error-reducing aligner](http://almlab.mit.edu/shera.html)
- [On the number of successes in indepdent trials](http://www.ressources-actuarielles.net/EXT/ISFA/1226.nsf/0/069d7ef52c771fd0c1257e1d002a65cc/$FILE/A3n23.pdf)
- [Testing Poisson binomial ditributions](https://people.csail.mit.edu/jayadev/papers/soda2014pbd.pdf)
- [On the distributions of sums of independent random variables](https://link.springer.com/chapter/10.1007%2F978-3-642-49750-6_10#page-1)
- [The fourier transform of Poisson multinomial distributions and its algorithmic applications](https://arxiv.org/abs/1511.03592)
- [The Poisson approximation to the poisson binomial distribution](https://projecteuclid.org/euclid.aoms/1177705799)

<!-- refs -->
[poisson-binomial-distribution]: https://en.wikipedia.org/wiki/Poisson_binomial_distribution
[edgar2015error]: https://academic.oup.com/bioinformatics/article/31/21/3476/194979/Error-filtering-pair-assembly-and-error-correction
[testing-poisson-binomial-distributions]: https://people.csail.mit.edu/jayadev/papers/soda2014pbd.pdf
