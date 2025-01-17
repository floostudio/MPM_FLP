package chat.floo.mpmflp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SONY_VAIO on 31-Oct-15.
 */
public class JustifiedTextView extends TextView implements Justify.Justified {

    @SuppressWarnings("unused")
    public JustifiedTextView(final @NotNull Context context) {
        super(context);
        super.setMovementMethod(new LinkMovementMethod());
    }

    @SuppressWarnings("unused")
    public JustifiedTextView(final @NotNull Context context, final AttributeSet attrs) {
        super(context, attrs);
        if (getMovementMethod() == null) super.setMovementMethod(new LinkMovementMethod());
    }

    @SuppressWarnings("unused")
    public JustifiedTextView(final @NotNull Context context,
                             final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (getMovementMethod() == null) super.setMovementMethod(new LinkMovementMethod());
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Make sure we don't call setupScaleSpans again if the measure was triggered
        // by setupScaleSpans itself.
        if (!mMeasuring) {
            final Typeface typeface = getTypeface();
            final float textSize = getTextSize();
            final float textScaleX = getTextScaleX();
            final boolean fakeBold = getPaint().isFakeBoldText();
            if (mTypeface != typeface ||
                    mTextSize != textSize ||
                    mTextScaleX != textScaleX ||
                    mFakeBold != fakeBold) {
                final int width = MeasureSpec.getSize(widthMeasureSpec);
                if (width > 0 && width != mWidth) {
                    mTypeface = typeface;
                    mTextSize = textSize;
                    mTextScaleX = textScaleX;
                    mFakeBold = fakeBold;
                    mWidth = width;
                    mMeasuring = true;
                    try {
                        // Setup ScaleXSpans on whitespaces to justify the text.
                        Justify.setupScaleSpans(this, mSpanStarts, mSpanEnds, mSpans);
                    }
                    finally {
                        mMeasuring = false;
                    }
                }
            }
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text,
                                 final int start, final int lengthBefore, final int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        final Layout layout = getLayout();
        if (layout != null) {
            Justify.setupScaleSpans(this, mSpanStarts, mSpanEnds, mSpans);
        }
    }

    @Override
    @NotNull
    public TextView getTextView() {
        return this;
    }

    @Override
    public float getMaxProportion() {
        return Justify.DEFAULT_MAX_PROPORTION;
    }

    private static final int MAX_SPANS = 512;

    private boolean mMeasuring = false;

    private Typeface mTypeface = null;
    private float mTextSize = 0f;
    private float mTextScaleX = 0f;
    private boolean mFakeBold = false;
    private int mWidth = 0;

    private int[] mSpanStarts = new int[MAX_SPANS];
    private int[] mSpanEnds = new int[MAX_SPANS];
    private Justify.ScaleSpan[] mSpans = new Justify.ScaleSpan[MAX_SPANS];

}