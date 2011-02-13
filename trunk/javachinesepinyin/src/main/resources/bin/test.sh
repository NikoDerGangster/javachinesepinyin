#!/bin/bash

java -cp ../lib/${project.build.finalName}.${project.packaging}:../config:. pinyin.PinyinTest $1
