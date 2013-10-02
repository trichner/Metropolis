package ch.k42.metropolis.minions;

/*
 * Copyright 2013 Michael McKnight. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.block.Chest;

public class UglyHacks
{
    private UglyHacks() {}

    public static void setChestName(Chest chest, String name)
    {
        try
        {
            Field inventoryField = chest.getClass().getDeclaredField("chest"); //This get's the CraftChest variable 'chest' which is the TileEntityChest that is stored within it
            inventoryField.setAccessible(true); //Allows you to access that field since it's declared as private

            Object teChest = inventoryField.get(chest);

            // Fetch the method we want
            Method a = getMethod(teChest.getClass(), "a", String.class);
            //The a(String) method sets the title of the chest
            a.invoke(teChest, name);
        }
        catch (Exception e) //This has to be here as the getDeclaredField(String) throws an exception if the input doesn't exist in the given class
        {
            e.printStackTrace();
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException
    {
        try
        {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e)
        {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null)
            {
                throw e;
            }
            else
            {
                return getField(superClass, fieldName);
            }
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?> ...params) throws NoSuchMethodException
    {
        try
        {
            return clazz.getDeclaredMethod(methodName, params);
        }
        catch (NoSuchMethodException e)
        {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null)
            {
                throw e;
            }
            else
            {
                return getMethod(superClass, methodName, params);
            }
        }
    }
}
