#!/bin/bash

java -cp ../lib/${project.build.finalName}.${project.packaging}:../config:. pinyin.PinyinToWord $1
java -cp ../lib/${project.build.finalName}.${project.packaging}:../config:. pinyin.WordToPinyin $1
