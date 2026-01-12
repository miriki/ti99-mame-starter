package com.miriki.ti99.mame.domain;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import com.miriki.ti99.mame.dto.MediaEntry;
import com.miriki.ti99.mame.ui.UiConstants;

/**
 * Base class for typed lists of {@link MediaEntry} objects.
 * <p>
 * Provides common operations such as lookup by display name, path resolution,
 * deduplication and sorting. Subclasses define the concrete media type.
 *
 * @param <T> the specific {@link MediaEntry} subtype
 */
public abstract class MediaEntryList<T extends MediaEntry> {

    // private static final Logger log = LoggerFactory.getLogger(MediaEntryList.class);

    /** Internal list of media entries. */
    protected final List<T> entries = new ArrayList<>();

    public List<T> getEntries() {
        return entries;
    }
    
    // -------------------------------------------------------------------------
    // Basic list operations
    // -------------------------------------------------------------------------

    /**
     * Replaces the current list of entries with the given list.
     *
     * @param newEntries the new entries to set
     */
    public void set(List<T> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
    }

    /**
     * Returns a defensive copy of all entries.
     *
     * @return a new list containing all entries
     */
    public List<T> getAll() {
        return new ArrayList<>(entries);
    }

    // -------------------------------------------------------------------------
    // Display helpers
    // -------------------------------------------------------------------------

    /**
     * Returns all distinct display names of the contained entries.
     *
     * @return list of display names
     */
    public List<String> getDisplayNames() {
        return entries.stream()
                .map(MediaEntry::getDisplayName)
                .distinct()
                .toList();
    }

    // -------------------------------------------------------------------------
    // Path resolution
    // -------------------------------------------------------------------------

    /**
     * Resolves the full media path for the given display name.
     *
     * @param displayName the display name to resolve
     * @return the full path, or {@code null} if not found or invalid
     */
    public Path resolveMediaPath(String displayName) {
    	// log.trace( "resolveMediaPath( displayName='{}' )", displayName );
        if (displayName == null
                || displayName.isBlank()
                || UiConstants.CBX_SEL_NONE.equals(displayName)) {
            return null;
        }

        T entry = findByDisplayName(displayName);
        if (entry == null) {
            return null;
        }

        // FIAD-Sonderfall: Alias "name.fiad" → Ordner "name"
        if (displayName.toLowerCase().endsWith(".fiad")) {
        	// log.trace( ".fiad detected, returning: '{}'", entry.getMediaPath().resolve(entry.getMediaName()) );
            return entry.getMediaPath().resolve(entry.getMediaName());
        }

        // Normale Medien
    	// log.trace( "normal file, returning: '{}'", entry.getFullPath() );
        return entry.getFullPath();
    }

    /**
     * Resolves the media path relative to a base directory.
     *
     * @param displayName the display name to resolve
     * @param basePath    the base directory
     * @return the relative path, or {@code null} if not found
     */
    public Path resolveMediaRelativePath(String displayName, Path workingDir) {

        if (displayName == null || displayName.isBlank())
            return null;

        if (UiConstants.CBX_SEL_NONE.equals(displayName))
            return null;

        MediaEntry entry = findByDisplayName(displayName);
        if (entry == null)
            return null;

        Path full;

        // FIAD-Sonderfall: Ordner statt Datei
        if (displayName.toLowerCase().endsWith(".fiad")) {
            full = entry.getMediaPath().resolve(entry.getMediaName());
        } else {
            full = entry.getFullPath();
        }

        try {
            return workingDir.relativize(full);
        } catch (IllegalArgumentException ex) {
            return full;
        }
    }
    
    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    /**
     * Finds an entry by its display name.
     *
     * @param displayName the display name to search for
     * @return the matching entry, or {@code null} if not found
     */
    public T findByDisplayName(String displayName) {
    	// log.debug( "findByDisplayName( displayName='{}' )", displayName );
        if (displayName == null) {
            return null;
        }

        for (T entry : entries) {
            if (displayName.equals(entry.getDisplayName())) {
            	// log.trace( "  +++ found" );
                return entry;
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Internal helpers for subclasses
    // -------------------------------------------------------------------------

    /**
     * Removes duplicate entries based on their media name (case-insensitive).
     * Keeps the first occurrence.
     */
    protected void deduplicate() {
        Map<String, T> unique = new LinkedHashMap<>();

        for (T e : entries) {
            unique.putIfAbsent(e.getMediaName().toLowerCase(), e);
        }

        entries.clear();
        entries.addAll(unique.values());
    }

    /**
     * Sorts entries alphabetically by media name (case-insensitive).
     */
    protected void sort() {
        entries.sort(Comparator.comparing(T::getMediaName, String.CASE_INSENSITIVE_ORDER));
    }
}
