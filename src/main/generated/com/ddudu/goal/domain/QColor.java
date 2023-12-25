package com.ddudu.goal.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QColor is a Querydsl query type for Color
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QColor extends BeanPath<Color> {

    private static final long serialVersionUID = 2088984191L;

    public static final QColor color = new QColor("color");

    public final StringPath code = createString("code");

    public QColor(String variable) {
        super(Color.class, forVariable(variable));
    }

    public QColor(Path<? extends Color> path) {
        super(path.getType(), path.getMetadata());
    }

    public QColor(PathMetadata metadata) {
        super(Color.class, metadata);
    }

}

