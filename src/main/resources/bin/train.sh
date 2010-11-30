#!/bin/bash

java -cp ../lib/${project.build.finalName}.${project.packaging}:../config:. com.apc.pinyin.PinyinToWord $1
java -cp ../lib/${project.build.finalName}.${project.packaging}:../config:. com.apc.pinyin.WordToPinyin $1
