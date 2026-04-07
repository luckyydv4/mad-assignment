package com.example.question4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final List<File> files;
    private final OnImageClickListener listener;
    private final LayoutInflater inflater;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final LruCache<String, Bitmap> memoryCache;

    public interface OnImageClickListener {
        void onImageClick(File file);
    }

    public ImageAdapter(Context context, List<File> files, OnImageClickListener listener) {
        this.files = files;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = files.get(position);
        holder.imageView.setImageBitmap(null);
        
        Bitmap cachedBitmap = memoryCache.get(file.getAbsolutePath());
        if (cachedBitmap != null) {
            holder.imageView.setImageBitmap(cachedBitmap);
        } else {
            executorService.execute(() -> {
                Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 200, 200);
                if (bitmap != null) {
                    memoryCache.put(file.getAbsolutePath(), bitmap);
                    holder.imageView.post(() -> holder.imageView.setImageBitmap(bitmap));
                }
            });
        }

        holder.itemView.setOnClickListener(v -> listener.onImageClick(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}