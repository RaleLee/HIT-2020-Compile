# HIT-2020-Compile
哈工大2020春季学期编译实验

文法部分：zch 

词法分析、语法分析：lzy

语义分析、GUI：cs

欢迎学弟学妹pr，issues交流~~~

## 词法分析

- 词法文件见Code/config/FA文件夹中的DFA与NFA
- 实现了全部基础功能和扩展功能
- 能够应用子集构造法接收ε-NFA转化为DFA
- 主要代码见Code/src/lexical文件夹中的LexicalAnalyzer
- 错误分析采用恐慌模式

## 语法分析

- 特色：使用LL1文法自顶向下实现（大部分已有仓库是基于LR1文法实现的）
- 语法文件见Code/config/Grammar文件夹中的LL1
- 实现了全部的基础功能与扩展功能
- 借助第三方库能够实现打印输出结果的树状图
- 主要代码见Code/src/grammar文件夹中的GrammarAnalyzer
- 错误分析采用ppt中的方法，使用FOLLOW集来构造sync同步标记

## 语义分析

