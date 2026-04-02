import pandas as pd

df = pd.read_csv(r'F:\frontend\machinelearn\final_ml_dataset.csv')
print('Shape:', df.shape)
print()
print('Columns:', df.columns.tolist())
print()
print('Dtypes:')
print(df.dtypes)
print()
print('First 3 rows:')
print(df.head(3).to_string())
print()
print('Target column value counts:')
for col in df.columns:
    if df[col].dtype == object or col == "skill_gap":
        print(f'--- {col} ---')
        print(df[col].value_counts())
