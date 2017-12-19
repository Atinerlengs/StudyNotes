## 拉取代码&编译
#### MTK平台
- 拉取代码<br>

- 编译<br>
```
1. source build/envsetup.sh
2. lunch full_bv206-userdebug
3. ./mk -d -f bv206_a n/r
```

#### 展讯平台
以9850为例
- 拉取代码<br> 

```
repo init --no-repo-verify -u ssh://你的名字@10.20.40.19:29418/FreemeOS/manifest -m MocorDroid7.0_Trunk_k44_17b_W17.31.5/custom_driver.xml
repo sync 
repo start --all master
```

- 编译<br>
```
1. 【userdebug】unzip proprietories-sp9850ka_2c20_cmcc3-userdebug.zip 或者【user】unzip proprietories-sp9850ka_2c20_cmcc3-user.zip
2. . build/envsetup.sh
3. lunch sp9850ka_2c20_cmcc3-userdebug
4. kheader
5. make -j8
```