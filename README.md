# S-Repair

Code release of ["From Minimum Change to Maximum Density: On Determining Near-Optimal S-Repair" (TKDE)](https://ieeexplore.ieee.org/abstract/document/10183830).

Parameters
----------
The input and output of **Heuristic** and **Relaxation** algorithms are:

Method

```
setK(K);
```

Input:

```
int K;  // the number of considered neighbors 
```

Output

```
ArrayList<Integer> detectedRowIndexList
```

Citation
----------
If you use this code for your research, please consider citing:

```
@article{sun2023minimum,
  title={From Minimum Change to Maximum Density: On Determining Near-Optimal S-Repair},
  author={Sun, Yu and Song, Shaoxu and Yuan, Xiaojie},
  journal={IEEE Transactions on Knowledge and Data Engineering},
  year={2023},
  publisher={IEEE}
}
```