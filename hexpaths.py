#!/usr/bin/env python
from collections import defaultdict
import numpy as np
from copy import deepcopy

graph = defaultdict(set)
base_angle = np.pi / 3


def add_edge(a, b, g=graph):
    g[a].add(b)
    g[b].add(a)


def turn(dir, t):
    maps = {
        "w": 0,
        "q": base_angle,
        "e": -base_angle,
        "a": 2 * base_angle,
        "d": -2 * base_angle,
    }
    return (dir + maps[t]) % (2 * np.pi)


def move(pos, dir):
    return np.round(100 * ((pos / 100) + np.array([np.cos(dir), np.sin(dir)]))).astype(
        int
    )


def turns_to_coords(turns):
    dir = 0
    cur = np.array([0, 0])
    path = [tuple(cur)]
    for t in turns:
        dir = turn(dir, t)
        cur = move(cur, dir)
        path.append(tuple(cur))
    return path


def turns_to_graph(turns):
    path = turns_to_coords(turns)
    for a, b in zip(path, path[1:]):
        add_edge(a, b)


def deg(v):
    return len(graph[v])


def vertices():
    return graph.keys()


def euler_step(cur, visited):
    paths = []
    for nxt in graph[cur]:
        if nxt not in visited[cur]:
            vst = deepcopy(visited)
            add_edge(cur, nxt, vst)
            ps = euler_step(nxt, vst)
            for p in ps:
                p.append(cur)
                paths.append(p)
    if not paths:
        paths.append([cur])
    return paths


def euler():
    n = sum(deg(v) for v in vertices()) // 2 + 1
    if any(deg(v) % 2 != 0 for v in vertices()):
        starts = [v for v in vertices() if deg(v) % 2 != 0]
    else:
        starts = list(vertices())
    res = []
    for v in starts:
        s = euler_step(v, defaultdict(set))
        res += s

    return [(r.reverse(), r)[1] for r in res if len(r) == n]


def edge_to_angle(a, b):
    diff = np.array(b) - np.array(a)
    return np.arctan2(diff[1], diff[0])


def normalize_angle(r):
    r = np.mod(r, 2 * np.pi)
    if r > np.pi:
        r -= 2 * np.pi
    return r


def r2d(r):
    return np.round(np.rad2deg(r))


def edge_to_turn(a, b, prev_angle):
    angle = edge_to_angle(a, b)
    angle_diff = normalize_angle(angle - prev_angle)
    tn = int(round(angle_diff / base_angle)) + 2
    turns = ["d", "e", "w", "q", "a", "s"]
    if tn > 5:
        print(tn, r2d(angle_diff), r2d(angle - prev_angle), r2d(angle), r2d(prev_angle))
    return turns[tn], angle


def path_to_turns(p):
    ang = 0
    turns = ""
    for a, b in zip(p, p[1:]):
        t, ang = edge_to_turn(a, b, ang)
        turns += t
    return turns


def turns_to_pattern(t):
    dir = t[0]
    pat = t[1:]
    dirmap = {
        "w": "EAST",
        "q": "NORTH_EAST",
        "a": "NORTH_WEST",
        "e": "SOUTH_EAST",
        "d": "SOUTH_WEST",
        "s": "WEST",
    }
    return dirmap[dir] + " " + pat


white_sun = "waadaaedwdqeee"
create_lava = "wdqaqedeqaqd"
dispel_rain = "wqawwaawdwqeeewwweeew"
gtp = "wwaqqqqqweeeeedwqqqwwwqqeqqwwwqq"
import sys

if sys.argv.__len__() < 2:
    raise AttributeError(
        "Probally the wrong error *but* you need a signature to calculate"
    )


def main():
    turns_to_graph(sys.argv[1])
    eu = euler()
    print(len(eu))
    cout = 0
    for p in eu:
        print(turns_to_pattern(path_to_turns(p)), end=",\n")


if __name__ == "__main__":
    main()
