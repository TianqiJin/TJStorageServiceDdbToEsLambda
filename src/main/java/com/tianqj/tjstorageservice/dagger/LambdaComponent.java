package com.tianqj.tjstorageservice.dagger;

import com.tianqj.tjstorageservice.handlers.DdbEventHandler;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = LambdaModule.class)
public interface LambdaComponent {
    DdbEventHandler getDdbEventHandler();
}
