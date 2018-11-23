# Expandablelayout [![](https://ci.novoda.com/buildStatus/icon?job=bintray-release)](https://ci.novoda.com/job/bintray-release/lastBuild/console) [![Download](https://api.bintray.com/packages/wangjinggm/maven/expandablelayout/images/download.svg) ](https://bintray.com/wangjinggm/maven/expandablelayout/_latestVersion) [![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](https://github.com/Lee-Wang-Jing/ExpandableLayout/blob/master/LICENSE) 

技术交流群：598627802，加群前请务必阅读[群行为规范](https://github.com/Lee-Wang-Jing/GroupStandard)     
有问题或者某种需求欢迎加群或者提issues，Thanks
----
# Features
1. 支持多个view展开收起
2. 支持设置展开收起位置
3. 支持设置展开收起为图片


# Dependencies
* Gradle
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
	
```groovy
implementation 'com.github.Lee-Wang-Jing:ExpandableLayout:1.0.5'
```
* Maven
```xml
<dependency>
	    <groupId>com.github.Lee-Wang-Jing</groupId>
	    <artifactId>ExpandableLayout</artifactId>
	    <version>Tag</version>
</dependency>
```

* Eclipse ADT请放弃治疗

# Screenshot
gif有一些失真，且网页加载速度慢，建议下载demo运行后查看效果。  

# Usage
首先添加再依赖后Sync。

## xml中引用
在xml中引用SwipeRecyclerView：
```xml
    <com.wangjing.expandablelayout.ExpandableTextview
           android:id="@+id/expand_text_view"
           android:layout_width="match_parent"
           android:layout_height="wrap_content"
           expandableTextView:collapsedText="收起"
           expandableTextView:expandText="展开"
           expandableTextView:animDuration="300"
           expandableTextView:animAlphaStart="1"
           expandableTextView:maxCollapsedLines="4">
   
           <TextView
               android:id="@+id/expandable_text"
               android:layout_width="match_parent"
               android:layout_height="wrap_content"
               android:layout_marginLeft="10dp"
               android:layout_marginRight="10dp"
               android:layout_marginTop="8dp"
               android:fontFamily="sans-serif-light"
               android:textColor="#666666"
               android:textSize="16sp" />
   
           <TextView
               android:id="@+id/expand_collapse"
               android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:layout_gravity="right|bottom"
               android:background="@android:color/transparent"
               android:padding="16dp"
               android:textColor="@color/colorPrimary"
               android:textSize="16sp" />
       </com.wangjing.expandablelayout.ExpandableTextview>
```
//支持设置展开收起为图片
```xml
     <com.wangjing.expandablelayout.ExpandableImageView
            android:id="@+id/expand_imageview"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            expandableTextView:collapsedDrawable="@mipmap/ic_launcher"
            expandableTextView:expandDrawable="@mipmap/ic_launcher"
            expandableTextView:animAlphaStart="1"
            expandableTextView:animDuration="300"
            expandableTextView:maxCollapsedLines="4">
    
            <TextView
                android:id="@+id/expandable_text"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginLeft="10dp"
                android:layout_marginRight="10dp"
                android:layout_marginTop="8dp"
                android:fontFamily="sans-serif-light"
                android:textColor="#666666"
                android:textSize="16sp" />
    
            <ImageView
                android:id="@+id/expand_collapse"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="right|bottom"
                android:padding="16dp" />
        </com.wangjing.expandablelayout.ExpandableImageView>
```
## 支持自定义的参数
```xml
<resources>

    <declare-styleable name="ExpandableTextView">
        <!--最大行数-->
        <attr name="maxCollapsedLines" format="integer" />
        <!--动画时间-->
        <attr name="animDuration" format="integer" />
        <!--动画alpha值，默认0.7f-->
        <attr name="animAlphaStart" format="float" />
        <!--展开时显示的文案，默认收起-->
        <attr name="collapsedText" format="string" />
        <!--收起时显示的文案，默认展开-->
        <attr name="expandText" format="string" />
        
         <!--展开时显示的图片，默认显示黑色箭头-->
         <attr name="collapsedDrawable" format="reference" />
         <!--收起时显示的图片，默认显示黑色箭头-->
         <attr name="expandDrawable" format="reference" />
         <!--内容文字区域点击是否可以展开收起，默认false，不可以-->
         <attr name="contentClick" format="boolean" />
         <!--展开还是收起状态，默认收起 true-->
         <attr name="collapsed" format="boolean" />
    </declare-styleable>

</resources>
```

## 使用方法

* 在普通Layout中的使用 
```java
ExpandableTextview expTv1 = (ExpandableTextview) rootView.findViewById(R.id.sample1)
                    .findViewById(R.id.expand_text_view);
expTv1.setText(getString(R.string.dummy_text1));

 ExpandableImageView expTv1 =  rootView.findViewById(R.id.sample1)
                    .findViewById(R.id.expand_imageview);
expTv1.setText(getString(R.string.dummy_text1));
```
* 在ListView、GridView或者RecyclerView等多个复用View中的使用     
初始化SparseBooleanArray
```java
  private final SparseBooleanArray mCollapsedStatus;
  public SampleTextListAdapter(Context context) {
        mContext  = context;
        mCollapsedStatus = new SparseBooleanArray();
  }
```
   
```java
 viewHolder.expandableTextView.setText(sampleStrings[position], mCollapsedStatus, position);
```
此处需要使用expandableTextView中含有三个参数的setText方法     
因为需要记录每个position的展开收起状态，否则会混乱

* 设置展开收起的监听：
```java
// 设置操作监听。
           ExpandableTextview expTv1 = (ExpandableTextview) rootView.findViewById(R.id.sample1)
                    .findViewById(R.id.expand_text_view);

           expTv1.setOnExpandStateChangeListener(new ExpandableTextview.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    Toast.makeText(getActivity(), isExpanded ? "Expanded" : "Collapsed", Toast.LENGTH_SHORT).show();
                }
            });
//或者
           ExpandableImageView expTv1 = (ExpandableImageView) rootView.findViewById(R.id.sample1)
                    .findViewById(R.id.expand_text_view);
           
           expTv1.setOnExpandStateChangeListener(new ExpandableImageView.OnExpandStateChangeListener() {
                           @Override
                           public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                               Toast.makeText(getActivity(), isExpanded ? "Expanded" : "Collapsed", Toast.LENGTH_SHORT).show();
                           }
                       });
 

```

# License
```text
Copyright 2017 Wang Jing

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```