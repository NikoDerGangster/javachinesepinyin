这是一个中文拼音输入法的Java实现，基于HMM模型，无词典，能完成拼音转汉字，和汉字转拼音的任务，并结合Edit Distance使其具有中文输入纠错功能。

---

## 开源拼音输入法的状态 ##
目前google code和sourceforge上有很多与拼音有关的项目，但是大多项目都没有完成，部分项目已经很久没有人维护了，同时真正可以被称为输入法的项目很少。使用统计模型的也有一些如：davepy，但是这些项目都没有明确的说明使用何种算法。有名的拼音输入法仅有ibus，scim，google拼音。所以javachinesepinyin作为一个入门级的输入法，还是非常不错的。[Wiki](http://code.google.com/p/javachinesepinyin/wiki/WhereAreWe)

## 目的 ##
目前搜狗拼音输入法已经开始发展云端输入法，也就是说，搜狗愿意为用户对大量的网络文本进行统计，这些统计结果必然无法直接在客户端运行。

我们目的是将javachinesepinyin应用到服务器端，能够为终端用户提供更好的输入体验，也能够为搜索引擎用户提供更好的输入体验，也可以对用户输入的错误进行很好的纠错。

## 最近更新 ##
现在已经将javachinesepinyin的绝大部分代码换成scala的了，呵呵！保留的java部分是方便大家可以将其作为demo，参照着与java集成。但是现在内存耗的更多了，需要彻底的改变ngram模型的数据结构和算法。如果各位有心人不吝赐教，鄙人将不胜感激！

## Milestones ##
2010-07-18:我们将javachinesepinyin部署到google appengine上，以web service的方式展示javachinesepinyin的性能，但是因为appengine的限制，我只能上传bigram的统计信息。测试可以点击[这里](http://951438.appspot.com/pinyin.jsp?txt=zhongwenpinyinshurufa)

---

## 文档 ##
算法原理和功能介绍：
[Chinese Pinyin Input Method](http://docs.google.com/present/edit?id=0AbbbdNFzwcADZGR3Z3N0NG1fMTk4M2hraGZjNmRw&hl=en)

[Chinese Pinyin Input Method 豆丁网](http://www.docin.com/p-63678358.html)

[项目编译](http://code.google.com/p/javachinesepinyin/wiki/HowToBuild)

any question, mailto: yingrui.f@gmail.com