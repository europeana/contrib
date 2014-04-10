#!/usr/bin/env python

import os
import sys
import numpy as np

import baker

def read_log(filename):
    records = []
    with open(filename) as f:
        for line in f:
            if line.startswith('#'):
                record = map(float, line.split()[1:])
                assert len(record) == 2
                records.append(record)

    return np.asarray(records)


def plot_convergence(ax, curves):
    for curve, label in curves:
        ax.plot(curve[:, 0], curve[:, 1], label=label)

    ax.legend()

    ax.grid(True, 'major', color='w', linestyle='-', linewidth=0.7)
    ax.grid(True, 'minor', color='0.95', linestyle='-', linewidth=0.2)


def plot_convergence_envelope(ax, curves):
    max_x = max(curve[-1, 0] for curve, _ in curves)

    for curve, label in curves:
        cx = np.hstack([curve[:, 0], max_x])
        cy = np.hstack([curve[:, 1], 0])
        ax.plot(cx, np.maximum.accumulate(cy), label=label)

    ax.legend(loc=4)

    ax.grid(True, 'major', color='w', linestyle='-', linewidth=0.7)
    ax.grid(True, 'minor', color='0.95', linestyle='-', linewidth=0.2)


@baker.command
def plot_logs(*logs_with_labels):
    from pylab import figure
    from matplotlib import rc
    rc('ps', useafm=True)
    rc('pdf', use14corefonts=True)
    rc('text', usetex=True)
    rc('font', family='sans-serif')
    rc('font', **{'sans-serif': ['Computer Modern']})

    try:
        from mpltools import style
        style.use('ggplot')
        rc('axes', grid=False)
    except ImportError:
        print >> sys.stderr, 'mpltools not installed, using standard (boring) style'

    fig = figure()
    curves = [(read_log(filename), label)
                      for filename, label in (ll.strip().split(':')
                                              for ll in logs_with_labels)]

    plot_convergence(fig.gca(), curves)
    fig.savefig('convergence.pdf', bbox_inches='tight')

    fig = figure()
    plot_convergence_envelope(fig.gca(), curves)
    fig.savefig('convergence_envelope.pdf', bbox_inches='tight')


if __name__ == '__main__':
    baker.run()
