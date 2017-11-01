package com.tao.hammer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author tyq
 * @version 1.0, 2017/11/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Range<T> implements Serializable {

    private T start;

    private T end;
}
