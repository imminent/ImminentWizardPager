package com.imminentmeals.android.wizard.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class StepPagerStrip extends View {

  public static interface OnPageSelectedListener {

    void onPageStripSelected(@Nonnegative int position);
  }

  public StepPagerStrip(Context context) {
    this(context, null, 0);
  }

  public StepPagerStrip(Context context, AttributeSet attributes) {
    this(context, attributes, 0);
  }

  public StepPagerStrip(Context context, AttributeSet attributes, int defStyle) {
    super(context, attributes, defStyle);

    final TypedArray a = context.obtainStyledAttributes(attributes, _ATTRIBUTES);
    assert a != null;
    _gravity = a.getInteger(0, _gravity);
    final boolean fill_horizontal =
        (_gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL;
    a.recycle();

    final Resources res = getResources();
    assert res != null;
    _tab_width = res.getDimensionPixelSize(R.dimen.step_pager_tab_width);
    _tab_height = res.getDimensionPixelSize(R.dimen.step_pager_tab_height);
    _tab_spacing = res.getDimensionPixelSize(R.dimen.step_pager_tab_spacing);

    if (fill_horizontal) {
      _tab_width =
          (getWidth() - getPaddingRight() - getPaddingLeft() - (_page_count - 1) * _tab_spacing)
              / _page_count;
    }

    _previous_tab_paint_brush = new Paint();
    _previous_tab_paint_brush.setColor(res.getColor(R.color.step_pager_previous_tab_color));

    _selected_tab_paint_brush = new Paint();
    _selected_tab_paint_brush.setColor(res.getColor(R.color.step_pager_selected_tab_color));

    _selected_last_paint_brush = new Paint();
    _selected_last_paint_brush.setColor(res.getColor(R.color.step_pager_selected_last_tab_color));

    _next_tab_paint = new Paint();
    _next_tab_paint.setColor(res.getColor(R.color.step_pager_next_tab_color));
  }

  public void pageSelectedListener(OnPageSelectedListener page_selected_listener) {
    _page_selected_listener = Optional.of(page_selected_listener);
  }

  public void removePageSelectedListener() {
    _page_selected_listener = Optional.absent();
  }

  public void numberOfPages(int count) {
    _page_count = count;
    final float total_width = _page_count * (_tab_width + _tab_spacing) - _tab_spacing;

    switch (_gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
      case Gravity.CENTER_HORIZONTAL:
        _total_left = (getWidth() - total_width) / 2;
        break;
      case Gravity.RIGHT:
        _total_left = getWidth() - getPaddingRight() - total_width;
        break;
      case Gravity.FILL_HORIZONTAL:
        _total_left = getPaddingLeft();
        break;
      default:
        _total_left = getPaddingLeft();
    }

    switch (_gravity & Gravity.VERTICAL_GRAVITY_MASK) {
      case Gravity.CENTER_VERTICAL:
        _reusable_rect.top = (int) (getHeight() - _tab_height) / 2;
        break;
      case Gravity.BOTTOM:
        _reusable_rect.top = getHeight() - getPaddingBottom() - _tab_height;
        break;
      default:
        _reusable_rect.top = getPaddingTop();
    }

    _reusable_rect.bottom = _reusable_rect.top + _tab_height;
    invalidate();

    // TODO: Set content description appropriately
  }

  public void currentPage(int currentPage) {
    _current_page = currentPage;

    invalidate();
    scrollCurrentPageIntoView();

    // TODO: Set content description appropriately
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (_page_count == 0) return;

    for (int i = 0; i < _page_count; i++) {
      _reusable_rect.left = _total_left + (i * (_tab_width + _tab_spacing));
      _reusable_rect.right = _reusable_rect.left + _tab_width;
      canvas.drawRect(_reusable_rect, i < _current_page? _previous_tab_paint_brush
                                                       : (i > _current_page? _next_tab_paint
                                                                           : (i == _page_count - 1
                                                                              ? _selected_last_paint_brush
                                                                              : _selected_tab_paint_brush)));
    }
  }

  @Override protected void onMeasure(int width_spec, int height_spec) {
    setMeasuredDimension(View.resolveSize((int) (_page_count * (_tab_width + _tab_spacing)
                                                     - _tab_spacing)
                                              + getPaddingLeft()
                                              + getPaddingRight(), width_spec),
                         View.resolveSize((int) _tab_height + getPaddingTop() + getPaddingBottom(),
                                          height_spec));
  }

  @Override protected void onSizeChanged(int width, int height, int old_width, int old_height) {
    scrollCurrentPageIntoView();
    super.onSizeChanged(width, height, old_width, old_height);
  }

  @Override public boolean onTouchEvent(@Nonnull MotionEvent event) {
    if (_page_selected_listener.isPresent()) {
      switch (event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
          int position = hitTest(event.getX());
          if (position >= 0) {
            _page_selected_listener.get().onPageStripSelected(position);
          }
          return true;
      }
    }
    return super.onTouchEvent(event);
  }

  private int hitTest(float x) {
    if (_page_count == 0) return -1;

    float total_width = _page_count * (_tab_width + _tab_spacing) - _tab_spacing;
    float total_left;
    boolean fill_horizontal = false;

    switch (_gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
      case Gravity.CENTER_HORIZONTAL:
        total_left = (getWidth() - total_width) / 2;
        break;
      case Gravity.RIGHT:
        total_left = getWidth() - getPaddingRight() - total_width;
        break;
      case Gravity.FILL_HORIZONTAL:
        total_left = getPaddingLeft();
        fill_horizontal = true;
        break;
      default:
        total_left = getPaddingLeft();
    }

    float tabWidth = _tab_width;
    if (fill_horizontal) {
      tabWidth =
          (getWidth() - getPaddingRight() - getPaddingLeft() - (_page_count - 1) * _tab_spacing)
              / _page_count;
    }

    float totalRight = total_left + (_page_count * (tabWidth + _tab_spacing));
    return x >= total_left && x <= totalRight && totalRight > total_left? (int) (((x - total_left)
                                                                                      / (totalRight
                                                                                             - total_left))
                                                                                     * _page_count)
                                                                        : -1;
  }

  private void scrollCurrentPageIntoView() {
    // TODO: only works with left gravity for now
//
//        float widthToActive = getPaddingLeft() + (_current_page + 1) * (_tab_width + _tab_spacing)
//                - _tab_spacing;
//        int viewWidth = getWidth();
//
//        int startScrollX = getScrollX();
//        int destScrollX = (widthToActive > viewWidth) ? (int) (widthToActive - viewWidth) : 0;
//
//        if (mScroller == null) {
//            mScroller = new Scroller(getContext());
//        }
//
//        mScroller.abortAnimation();
//        mScroller.startScroll(startScrollX, 0, destScrollX - startScrollX, 0);
//        postInvalidate();
  }

  private float _total_left;
  private int   _page_count;
  private int   _current_page;

  private int _gravity = Gravity.LEFT | Gravity.TOP;
  private float _tab_width;
  private float _tab_height;
  private float _tab_spacing;

  private Paint _previous_tab_paint_brush;
  private Paint _selected_tab_paint_brush;
  private Paint _selected_last_paint_brush;
  private Paint _next_tab_paint;

  private RectF _reusable_rect = new RectF();

  //private Scroller mScroller;

  private Optional<OnPageSelectedListener> _page_selected_listener;
  private static final int[] _ATTRIBUTES = new int[] { android.R.attr.gravity };
}
