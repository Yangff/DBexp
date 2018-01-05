package com.totem.storage;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Page {
    public static final int page_size = 4096;
    public byte[] buffer;
    public long page;

    public boolean available;
    public boolean dirty;

    private RandomAccessFile fs;

    public Page(RandomAccessFile file, long page){
        buffer = new byte[page_size];
        this.page = page;
        this.dirty = false;
        this.available = false;
        gotoPage(page);
    }

    public boolean replace(long page){
        if (!available) {
            gotoPage(page);
            return true;
        }
        if (page == this.page)
            return true;
        if (dirty)
            if (!writePage())
                return false;
        return gotoPage(page);
    }

    public boolean sync(){
        if (dirty)
            return writePage();
        return true;
    }

    // not thread safe warning
    private boolean gotoPage(long _page){
        try {
            if ((1+_page) * page_size < fs.length()) {
                fs.setLength((1 + _page) * page_size);
            }
            fs.seek(_page*page_size);
            fs.read(buffer, 0, page_size);
            page = _page;
            dirty = false;
            available = true;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean writePage() {
        try {
            if ((1+page) * page_size < fs.length()) {
                fs.setLength((1 + page) * page_size);
            }
            fs.seek(page * page_size);
            fs.write(buffer, 0, page_size);
            dirty = false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
