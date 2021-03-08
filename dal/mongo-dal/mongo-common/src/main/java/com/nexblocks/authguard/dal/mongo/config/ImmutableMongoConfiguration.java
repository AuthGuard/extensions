package com.nexblocks.authguard.dal.mongo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Var;
import org.immutables.value.Generated;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable implementation of {@link MongoConfiguration}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableMongoConfiguration.builder()}.
 */
@Generated(from = "MongoConfiguration", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
@CheckReturnValue
public final class ImmutableMongoConfiguration implements MongoConfiguration {
  private final String database;
  private final String username;
  private final String password;
  private final String connectionString;
  private final Map<String, String> collections;

  private ImmutableMongoConfiguration(
      String database,
      String username,
      String password,
      String connectionString,
      Map<String, String> collections) {
    this.database = database;
    this.username = username;
    this.password = password;
    this.connectionString = connectionString;
    this.collections = collections;
  }

  /**
   * @return The value of the {@code database} attribute
   */
  @JsonProperty("database")
  @Override
  public String getDatabase() {
    return database;
  }

  /**
   * @return The value of the {@code username} attribute
   */
  @JsonProperty("username")
  @Override
  public String getUsername() {
    return username;
  }

  /**
   * @return The value of the {@code password} attribute
   */
  @JsonProperty("password")
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * @return The value of the {@code connectionString} attribute
   */
  @JsonProperty("connectionString")
  @Override
  public String getConnectionString() {
    return connectionString;
  }

  /**
   * @return The value of the {@code collections} attribute
   */
  @JsonProperty("collections")
  @Override
  public Map<String, String> getCollections() {
    return collections;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MongoConfiguration#getDatabase() database} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for database (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMongoConfiguration withDatabase(String value) {
    if (Objects.equals(this.database, value)) return this;
    return new ImmutableMongoConfiguration(value, this.username, this.password, this.connectionString, this.collections);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MongoConfiguration#getUsername() username} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for username (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMongoConfiguration withUsername(String value) {
    if (Objects.equals(this.username, value)) return this;
    return new ImmutableMongoConfiguration(this.database, value, this.password, this.connectionString, this.collections);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MongoConfiguration#getPassword() password} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for password (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMongoConfiguration withPassword(String value) {
    if (Objects.equals(this.password, value)) return this;
    return new ImmutableMongoConfiguration(this.database, this.username, value, this.connectionString, this.collections);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MongoConfiguration#getConnectionString() connectionString} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for connectionString (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMongoConfiguration withConnectionString(String value) {
    if (Objects.equals(this.connectionString, value)) return this;
    return new ImmutableMongoConfiguration(this.database, this.username, this.password, value, this.collections);
  }

  /**
   * Copy the current immutable object by replacing the {@link MongoConfiguration#getCollections() collections} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the collections map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMongoConfiguration withCollections(Map<String, ? extends String> entries) {
    if (this.collections == entries) return this;
    Map<String, String> newValue = createUnmodifiableMap(true, false, entries);
    return new ImmutableMongoConfiguration(this.database, this.username, this.password, this.connectionString, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableMongoConfiguration} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableMongoConfiguration
        && equalTo((ImmutableMongoConfiguration) another);
  }

  private boolean equalTo(ImmutableMongoConfiguration another) {
    return Objects.equals(database, another.database)
        && Objects.equals(username, another.username)
        && Objects.equals(password, another.password)
        && Objects.equals(connectionString, another.connectionString)
        && collections.equals(another.collections);
  }

  /**
   * Computes a hash code from attributes: {@code database}, {@code username}, {@code password}, {@code connectionString}, {@code collections}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    @Var int h = 5381;
    h += (h << 5) + Objects.hashCode(database);
    h += (h << 5) + Objects.hashCode(username);
    h += (h << 5) + Objects.hashCode(password);
    h += (h << 5) + Objects.hashCode(connectionString);
    h += (h << 5) + collections.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code MongoConfiguration} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "MongoConfiguration{"
        + "database=" + database
        + ", username=" + username
        + ", password=" + password
        + ", connectionString=" + connectionString
        + ", collections=" + collections
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "MongoConfiguration", generator = "Immutables")
  @Deprecated
  @SuppressWarnings("Immutable")
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements MongoConfiguration {
    @Nullable String database;
    @Nullable String username;
    @Nullable String password;
    @Nullable String connectionString;
    @Nullable Map<String, String> collections = Collections.emptyMap();
    @JsonProperty("database")
    public void setDatabase(String database) {
      this.database = database;
    }
    @JsonProperty("username")
    public void setUsername(String username) {
      this.username = username;
    }
    @JsonProperty("password")
    public void setPassword(String password) {
      this.password = password;
    }
    @JsonProperty("connectionString")
    public void setConnectionString(String connectionString) {
      this.connectionString = connectionString;
    }
    @JsonProperty("collections")
    public void setCollections(Map<String, String> collections) {
      this.collections = collections;
    }
    @Override
    public String getDatabase() { throw new UnsupportedOperationException(); }
    @Override
    public String getUsername() { throw new UnsupportedOperationException(); }
    @Override
    public String getPassword() { throw new UnsupportedOperationException(); }
    @Override
    public String getConnectionString() { throw new UnsupportedOperationException(); }
    @Override
    public Map<String, String> getCollections() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableMongoConfiguration fromJson(Json json) {
    Builder builder = ImmutableMongoConfiguration.builder();
    if (json.database != null) {
      builder.database(json.database);
    }
    if (json.username != null) {
      builder.username(json.username);
    }
    if (json.password != null) {
      builder.password(json.password);
    }
    if (json.connectionString != null) {
      builder.connectionString(json.connectionString);
    }
    if (json.collections != null) {
      builder.putAllCollections(json.collections);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link MongoConfiguration} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable MongoConfiguration instance
   */
  public static ImmutableMongoConfiguration copyOf(MongoConfiguration instance) {
    if (instance instanceof ImmutableMongoConfiguration) {
      return (ImmutableMongoConfiguration) instance;
    }
    return ImmutableMongoConfiguration.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableMongoConfiguration ImmutableMongoConfiguration}.
   * <pre>
   * ImmutableMongoConfiguration.builder()
   *    .database(String | null) // nullable {@link MongoConfiguration#getDatabase() database}
   *    .username(String | null) // nullable {@link MongoConfiguration#getUsername() username}
   *    .password(String | null) // nullable {@link MongoConfiguration#getPassword() password}
   *    .connectionString(String | null) // nullable {@link MongoConfiguration#getConnectionString() connectionString}
   *    .putCollections|putAllCollections(String => String) // {@link MongoConfiguration#getCollections() collections} mappings
   *    .build();
   * </pre>
   * @return A new ImmutableMongoConfiguration builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builds instances of type {@link ImmutableMongoConfiguration ImmutableMongoConfiguration}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "MongoConfiguration", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private @Nullable String database;
    private @Nullable String username;
    private @Nullable String password;
    private @Nullable String connectionString;
    private Map<String, String> collections = new LinkedHashMap<String, String>();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code MongoConfiguration} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    public final Builder from(MongoConfiguration instance) {
      Objects.requireNonNull(instance, "instance");
      String databaseValue = instance.getDatabase();
      if (databaseValue != null) {
        database(databaseValue);
      }
      String usernameValue = instance.getUsername();
      if (usernameValue != null) {
        username(usernameValue);
      }
      String passwordValue = instance.getPassword();
      if (passwordValue != null) {
        password(passwordValue);
      }
      String connectionStringValue = instance.getConnectionString();
      if (connectionStringValue != null) {
        connectionString(connectionStringValue);
      }
      putAllCollections(instance.getCollections());
      return this;
    }

    /**
     * Initializes the value for the {@link MongoConfiguration#getDatabase() database} attribute.
     * @param database The value for database (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    @JsonProperty("database")
    public final Builder database(String database) {
      this.database = database;
      return this;
    }

    /**
     * Initializes the value for the {@link MongoConfiguration#getUsername() username} attribute.
     * @param username The value for username (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    @JsonProperty("username")
    public final Builder username(String username) {
      this.username = username;
      return this;
    }

    /**
     * Initializes the value for the {@link MongoConfiguration#getPassword() password} attribute.
     * @param password The value for password (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    @JsonProperty("password")
    public final Builder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * Initializes the value for the {@link MongoConfiguration#getConnectionString() connectionString} attribute.
     * @param connectionString The value for connectionString (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    @JsonProperty("connectionString")
    public final Builder connectionString(String connectionString) {
      this.connectionString = connectionString;
      return this;
    }

    /**
     * Put one entry to the {@link MongoConfiguration#getCollections() collections} map.
     * @param key The key in the collections map
     * @param value The associated value in the collections map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    public final Builder putCollections(String key, String value) {
      this.collections.put(
          Objects.requireNonNull(key, "collections key"),
          Objects.requireNonNull(value, "collections value"));
      return this;
    }

    /**
     * Put one entry to the {@link MongoConfiguration#getCollections() collections} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    public final Builder putCollections(Map.Entry<String, ? extends String> entry) {
      String k = entry.getKey();
      String v = entry.getValue();
      this.collections.put(
          Objects.requireNonNull(k, "collections key"),
          Objects.requireNonNull(v, "collections value"));
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link MongoConfiguration#getCollections() collections} map. Nulls are not permitted
     * @param entries The entries that will be added to the collections map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    @JsonProperty("collections")
    public final Builder collections(Map<String, ? extends String> entries) {
      this.collections.clear();
      return putAllCollections(entries);
    }

    /**
     * Put all mappings from the specified map as entries to {@link MongoConfiguration#getCollections() collections} map. Nulls are not permitted
     * @param entries The entries that will be added to the collections map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue
    public final Builder putAllCollections(Map<String, ? extends String> entries) {
      for (Map.Entry<String, ? extends String> e : entries.entrySet()) {
        String k = e.getKey();
        String v = e.getValue();
        this.collections.put(
            Objects.requireNonNull(k, "collections key"),
            Objects.requireNonNull(v, "collections value"));
      }
      return this;
    }

    /**
     * Builds a new {@link ImmutableMongoConfiguration ImmutableMongoConfiguration}.
     * @return An immutable instance of MongoConfiguration
     * @throws IllegalStateException if any required attributes are missing
     */
    public ImmutableMongoConfiguration build() {
      return new ImmutableMongoConfiguration(
          database,
          username,
          password,
          connectionString,
          createUnmodifiableMap(false, false, collections));
    }
  }

  private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
    switch (map.size()) {
    case 0: return Collections.emptyMap();
    case 1: {
      Map.Entry<? extends K, ? extends V> e = map.entrySet().iterator().next();
      K k = e.getKey();
      V v = e.getValue();
      if (checkNulls) {
        Objects.requireNonNull(k, "key");
        Objects.requireNonNull(v, "value");
      }
      if (skipNulls && (k == null || v == null)) {
        return Collections.emptyMap();
      }
      return Collections.singletonMap(k, v);
    }
    default: {
      Map<K, V> linkedMap = new LinkedHashMap<>(map.size());
      if (skipNulls || checkNulls) {
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
          K k = e.getKey();
          V v = e.getValue();
          if (skipNulls) {
            if (k == null || v == null) continue;
          } else if (checkNulls) {
            Objects.requireNonNull(k, "key");
            Objects.requireNonNull(v, "value");
          }
          linkedMap.put(k, v);
        }
      } else {
        linkedMap.putAll(map);
      }
      return Collections.unmodifiableMap(linkedMap);
    }
    }
  }
}
