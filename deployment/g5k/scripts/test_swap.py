import logging
import traceback
from operator import itemgetter
from pathlib import Path
from typing import Dict

from execo_engine import ParamSweeper, sweep
from enoslib import *

parameters = dict(
    nb_worker=[1, 2, 4, 8, 12, 16, 24, 32],
    size=[1, 10, 100, 1000, 10000, 100000, 1000000, 10000000]
)

sweeps = sweep(parameters)
sweeper = ParamSweeper(
    persistence_dir=str(Path("sweeps")), sweeps=sweeps, save_sweeps=True
)

m = max(parameters["nb_worker"])
print(m)