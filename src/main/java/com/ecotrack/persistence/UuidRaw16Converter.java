package com.ecotrack.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.ByteBuffer;
import java.util.UUID;

@Converter(autoApply = true)
public class UuidRaw16Converter implements AttributeConverter<UUID, byte[]> {

  @Override
  public byte[] convertToDatabaseColumn(UUID attribute) {
    if (attribute == null) return null;
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(attribute.getMostSignificantBits());
    bb.putLong(attribute.getLeastSignificantBits());
    return bb.array();
  }

  @Override
  public UUID convertToEntityAttribute(byte[] dbData) {
    if (dbData == null) return null;
    ByteBuffer bb = ByteBuffer.wrap(dbData);
    long high = bb.getLong();
    long low = bb.getLong();
    return new UUID(high, low);
  }
}
