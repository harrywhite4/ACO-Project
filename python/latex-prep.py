from random import shuffle
dataIn = open("data.csv","r")
numMachines = 3
numJobs = 3
minMakeSpan = 11
jobs_last = [0] * numJobs
machines_last = [0] * numMachines
jobs_patterns = ["horizontal lines","vertical lines", "north east lines", "dots","grid", "north west lines", "crosshatch","crosshatch dots","bricks","fivepointed starts"]
jobs_colors = ["red","orange","green","yellow","blue","purple"]
shuffle(jobs_patterns)
shuffle(jobs_colors)
initialLine = dataIn.readline()
numMachines, minMakeSpan, numJobs = [int(x) for x in initialLine.split(",")] 
lines = [line.rstrip('\n') for line in dataIn.readlines()]

axis_raw = "\\draw[->] (0,0) -- ({0},0) node[anchor=west] {{$$Time$$}};\n\\draw[->] (0,0) -- (0,{1}) node[anchor=east] {{}};"
time_str = "\draw "
machine_str = "\draw "
for i in range(numMachines):
	machine_str += "(0, {0}) node[anchor=east] {{Machine {1}}}".format(i+0.5, numMachines-i) 
machine_str += ";"
for i in range(minMakeSpan+1):
	time_str += "({0}, 0) node[anchor=north] {{{1}}}".format(i, i)
time_str += ";"

axis_str = axis_raw.format(minMakeSpan+1, numMachines+1)
out = open("example.tex","w")
out.write("\\begin{tikzpicture}\n")
out.write(axis_str)
out.write(time_str)
out.write(machine_str)
job_raw = "\\draw [pattern={4}, fill={5}] ({0},{1}) rectangle ({2},{3});"
for l in lines:
	machine, job, time = [int(x) for x in l.split(",")]
	job -= 1
	start_time = max(jobs_last[job], machines_last[machine])
	job_str = job_raw.format(start_time, numMachines - machine - 1, start_time+time, numMachines - machine, jobs_patterns[job], jobs_colors[job])
	jobs_last[job] = start_time + time
	machines_last[machine] = start_time+time
	out.write(job_str)
out.write("\\end{tikzpicture}\n\\begin{tikzpicture}\n")
job_leg_raw = "\\draw [pattern={0}, fill={1}] (0,{2}) rectangle (0.5,{3})\n(0.5,{4}) node[anchor=west] {{Job {3}}};"
for job in range(numJobs):
	job_leg = job_leg_raw.format(jobs_patterns[job], jobs_colors[job], job, job+1, job+0.5)
	out.write(job_leg)
out.write("\\end{tikzpicture}\n")
out.close()
