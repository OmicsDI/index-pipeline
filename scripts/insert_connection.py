import pandas


df = pandas.read_csv('/home/gaur/Downloads/aj-normalised-connectivity-score-2018-09-23.txt', sep='\t',low_memory=False)
print(df.head())
