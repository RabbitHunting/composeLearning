package com.wbl.composelearning


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ListPicker(
    data: List<T>,
    selectIndex: Int,
    visibleCount: Int,
    modifier: Modifier = Modifier,
    onSelect: (index: Int, item: T) -> Unit,
    content: @Composable (item: T) -> Unit,
) {
    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val pickerHeight = maxHeight
        val size = data.size
        val itemHeight = pickerHeight / visibleCount
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = selectIndex
        )
        val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
        val dataPickerCoroutineScope = rememberCoroutineScope()
        LazyColumn(
            modifier = Modifier,
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
        ) {
            //根据显示的位数显示其展示的Item数量 比如显示4但是会在第三个为选中的状态
            for (i in 1..visibleCount / 2) {
                item {
                    Surface(modifier = Modifier.height(itemHeight)) {}
                }
            }
            items(size) { index ->
                if (firstVisibleItemIndex >= size) {
                    onSelect(size - 1, data[size - 1])
                } else {
                    onSelect(firstVisibleItemIndex, data[firstVisibleItemIndex])
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    content(data[index])
                }
            }
            for (i in 1..visibleCount / 2) {
                item {
                    Surface(modifier = Modifier.height(itemHeight)) {}
                }
            }
        }

        if (listState.isScrollInProgress) {

            LaunchedEffect(Unit) {
                //只会调用一次，相当于滚动开始
            }
            DisposableEffect(Unit) {
                onDispose {
                    try {
                        //	滑动结束时给状态赋值，并自动对齐
                        dataPickerCoroutineScope.launch {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }


    }
}

