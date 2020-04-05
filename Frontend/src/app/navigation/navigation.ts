import { FuseNavigation } from '@fuse/types';

export const navigation: FuseNavigation[] = [
    {
        id       : 'menu',
        title    : 'Main menu',
        type     : 'group',
        children : [
            {
                id       : 'home',
                title    : 'Home',
                type     : 'item',
                icon     : 'home',
                url      : '/',
            },
        ]
    },
];
