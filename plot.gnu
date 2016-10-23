#!/usr/bin/gnuplot
set title 'Makespan per Iteration'
set ylabel 'Makespan'
set xlabel 'Iteration'
set term png
set output 'plot.png'
set datafile separator ","
set yrange[0:20 < * < 100]
plot 'plot.csv' using 1:2 title 'Global best makespan', "plot.csv" using 1:3 title 'Generation best makespan' with linespoints
