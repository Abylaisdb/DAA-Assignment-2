import csv
import os
from collections import defaultdict

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

TABLES = "results/tables"
PLOTS = "results/plots"
os.makedirs(PLOTS, exist_ok=True)


def read_csv(name):
    path = os.path.join(TABLES, name)
    with open(path, newline="") as f:
        return list(csv.DictReader(f))


def save(fig, name):
    path = os.path.join(PLOTS, name)
    fig.savefig(path, dpi=150, bbox_inches="tight")
    plt.close(fig)
    print("wrote", path)


rows = read_csv("workload1_random_access.csv")
by_struct = defaultdict(lambda: ([], []))
by_struct_acc = defaultdict(lambda: ([], []))
for r in rows:
    ns, ts = by_struct[r["structure"]]
    ns.append(int(r["n"]))
    ts.append(float(r["avg_time_ns"]) / 1000.0)
    ns2, accs = by_struct_acc[r["structure"]]
    ns2.append(int(r["n"]))
    accs.append(int(r["total_accesses"]))

fig, ax = plt.subplots()
for struct, (ns, ts) in by_struct.items():
    ax.plot(ns, ts, marker="o", label=struct)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("avg time for 10,000 get(index) calls (microseconds)")
ax.set_title("Workload 1: Random Access - Execution Time vs n")
ax.legend()
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload1_time_vs_n.png")

fig, ax = plt.subplots()
for struct, (ns, accs) in by_struct_acc.items():
    ax.plot(ns, accs, marker="o", label=struct)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("total element accesses / pointer hops (10,000 gets)")
ax.set_title("Workload 1: Random Access - Operations vs n")
ax.legend()
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload1_ops_vs_n.png")


rows = read_csv("workload2_search.csv")
by_struct_t = defaultdict(lambda: ([], []))
by_struct_c = defaultdict(lambda: ([], []))
for r in rows:
    ns, ts = by_struct_t[r["structure"]]
    ns.append(int(r["n"]))
    ts.append(float(r["avg_time_ns"]) / 1000.0)
    ns2, cs = by_struct_c[r["structure"]]
    ns2.append(int(r["n"]))
    cs.append(int(r["total_comparisons"]))

fig, ax = plt.subplots()
for struct, (ns, ts) in by_struct_t.items():
    ax.plot(ns, ts, marker="o", label=struct)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("avg time for 1,000 contains(x) calls (microseconds)")
ax.set_title("Workload 2: Search - Execution Time vs n")
ax.legend()
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload2_time_vs_n.png")

fig, ax = plt.subplots()
for struct, (ns, cs) in by_struct_c.items():
    ax.plot(ns, cs, marker="o", label=struct)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("total comparisons (1,000 searches)")
ax.set_title("Workload 2: Search - Comparisons vs n")
ax.legend()
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload2_ops_vs_n.png")


rows = read_csv("workload3_insert_remove.csv")
series_t = defaultdict(lambda: ([], []))
series_m = defaultdict(lambda: ([], []))
for r in rows:
    key = f'{r["structure"]}-{r["operation"]}-{r["position"]}'
    ns, ts = series_t[key]
    ns.append(int(r["n"]))
    ts.append(float(r["avg_time_ns"]) / 1000.0)
    ns2, ms = series_m[key]
    ns2.append(int(r["n"]))
    ms.append(int(r["total_moves"]))

fig, ax = plt.subplots(figsize=(8, 6))
for key, (ns, ts) in sorted(series_t.items()):
    ax.plot(ns, ts, marker="o", label=key)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("avg time for 1,000 ops (microseconds)")
ax.set_title("Workload 3: Insertion/Removal - Execution Time vs n")
ax.legend(fontsize=8, ncol=2)
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload3_time_vs_n.png")

fig, ax = plt.subplots(figsize=(8, 6))
for key, (ns, ms) in sorted(series_m.items()):
    ax.plot(ns, ms, marker="o", label=key)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("total element moves / pointer relinks")
ax.set_title("Workload 3: Insertion/Removal - Moves vs n")
ax.legend(fontsize=8, ncol=2)
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload3_ops_vs_n.png")


rows = read_csv("workload4_heap.csv")
series_t = defaultdict(lambda: ([], []))
series_c = defaultdict(lambda: ([], []))
for r in rows:
    ns, ts = series_t[r["phase"]]
    ns.append(int(r["n"]))
    ts.append(float(r["avg_time_ns"]) / 1000.0)
    ns2, cs = series_c[r["phase"]]
    ns2.append(int(r["n"]))
    cs.append(int(r["total_comparisons"]))

fig, ax = plt.subplots()
for phase, (ns, ts) in series_t.items():
    ax.plot(ns, ts, marker="o", label=phase)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("avg time for n operations (microseconds)")
ax.set_title("Workload 4: Min-Heap - Execution Time vs n")
ax.legend()
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload4_time_vs_n.png")

fig, ax = plt.subplots()
for phase, (ns, cs) in series_c.items():
    ax.plot(ns, cs, marker="o", label=phase)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (elements)")
ax.set_ylabel("total comparisons")
ax.set_title("Workload 4: Min-Heap - Comparisons vs n")
ax.legend()
ax.grid(True, which="both", alpha=0.3)
save(fig, "workload4_ops_vs_n.png")

print("All plots generated.")