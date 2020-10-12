package io.agrest.cayenne.qualifier;

import io.agrest.base.protocol.CayenneExp;
import org.apache.cayenne.exp.Expression;

import java.util.List;

/**
 * @since 3.3
 */
public interface IQualifierParser {

    Expression parse(List<CayenneExp> qualifiers);
}