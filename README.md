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
compile 'com.wangjing:expandablelayout:1.0.1'
```
* Maven
```xml
<dependency>
  <groupId>com.wangjing</groupId>
  <artifactId>expandablelayout</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
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
    <com.wangjing.recyclerview_drag.DragRecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```
## 使用拖拽和侧滑删除功能
拖拽和侧滑删除的功能默认关闭的，所以先要打开功能：
```java
swipeRecyclerView.setLongPressDragEnabled(true); // 开启拖拽。
```
然后用户就可以长按拖拽`Item`和侧滑删除`Item`了，我们可以监听用户的操作：
```java
// 设置操作监听。
swipeRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。

OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // Item被拖拽时，交换数据，并更新adapter。
        Collections.swap(mDataList, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        // Item被侧滑删除时，删除数据，并更新adapter。
        mDataList.remove(position);
        adapter.notifyItemRemoved(position);
    }
};
```

**使用`Grid`形式的`RecyclerView`拖拽`Item`时特别注意，因为`Grid`的`Item`可以跨`position`拖拽，所以`onItemMove()`方法体有所不同：**
```java
@Override
public boolean onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition)
        for (int i = fromPosition; i < toPosition; i++)
            Collections.swap(mDataList, i, i + 1);
    else
        for (int i = fromPosition; i > toPosition; i--)
            Collections.swap(mDataList, i, i - 1);

    mMenuAdapter.notifyItemMoved(fromPosition, toPosition);
    return true;
}
```
我们还可以监听用户的侧滑删除和拖拽Item时的手指状态：
```java
/**
 * Item的拖拽/侧滑删除时，手指状态发生变化监听。
 */
private OnItemStateChangedListener stateChangedListener = (viewHolder, actionState) -> {
    if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
        // 状态：正在拖拽。
    } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
        // 状态：滑动删除。
    } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
        // 状态：手指松开。
    }
};
```
## 触摸拖拽 & 触摸侧滑删除
想用户触摸到某个`Item`的`View`时就开始拖拽实现也很简单。  

* 触摸拖拽
```java
swipeRecyclerView.startDrag(ViewHolder);
```
这里只要传入当前触摸`Item`对应的`ViewHolder`即可立即开始拖拽。

* 设置某个position不能拖拽
添加监听
```java
  recyclerview.setOnItemMovementListener(onItemMovementListener);
```
```java
   /**
       * 当Item被移动之前。
       */
      public static OnItemMovementListener onItemMovementListener = new OnItemMovementListener() {
          /**
           * 当Item在移动之前，获取拖拽的方向。
           * @param recyclerView     {@link RecyclerView}.
           * @param targetViewHolder target ViewHolder.
           * @return
           */
          @Override
          public int onDragFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder) {
              // 我们让第一个不能拖拽。
              if (targetViewHolder.getAdapterPosition() == 0) {
                  return OnItemMovementListener.INVALID;// 返回无效的方向。
              }
  
              RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
  //            if (layoutManager instanceof LinearLayoutManager) {// 如果是LinearLayoutManager。
  //                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
  //                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {// 横向的List。
  //                    return (OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT); // 只能左右拖拽。
  //                } else {// 竖向的List。
  //                    return OnItemMovementListener.UP | OnItemMovementListener.DOWN; // 只能上下拖拽。
  //                }
  //            }
  //            else
              if (layoutManager instanceof GridLayoutManager) {// 如果是Grid。
                  return OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT | OnItemMovementListener.UP |
                          OnItemMovementListener.DOWN; // 可以上下左右拖拽。
              }
              return OnItemMovementListener.INVALID;// 返回无效的方向。
          }
  
          @Override
          public int onSwipeFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder) {
  //            // 我们让第一个不能滑动删除。
  //            if (targetViewHolder.getAdapterPosition() == 0) {
  //                return OnItemMovementListener.INVALID;// 返回无效的方向。
  //            }
  //
  //            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
  //            if (layoutManager instanceof LinearLayoutManager) {// 如果是LinearLayoutManager
  //                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
  //                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {// 横向的List。
  //                    return OnItemMovementListener.UP | OnItemMovementListener.DOWN; // 只能上下滑动删除。
  //                } else {// 竖向的List。
  //                    return OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT; // 只能左右滑动删除。
  //                }
  //            }
              return OnItemMovementListener.INVALID;// 其它均返回无效的方向。
          }
      };
```
这里可以设置某个position不能拖拽或者滑动删除(Features版本支持)


# Thanks
* [SwipeMenu](https://github.com/TUBB/SwipeMenu/)
* [SwipeRecyclerView](https://github.com/yanzhenjie/SwipeRecyclerView)

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