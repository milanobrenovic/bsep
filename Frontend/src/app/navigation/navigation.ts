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
                url      : '/pages/home',
            },
            {
                id       : 'add-subject',
                title    : 'Add subject',
                type     : 'item',
                icon     : 'add_circle_outline',
                url      : '/pages/add-subject',
            },
            {
                id       : 'create-certificate',
                title    : 'Create new certificate',
                type     : 'item',
                icon     : 'add_box',
                url      : '/pages/create-certificate',
            },
            {
                id       : 'create-self-signed-certificate',
                title    : 'Create self-signed certificate',
                type     : 'item',
                icon     : 'add_to_photos',
                url      : '/pages/create-self-signed-certificate',
            },
            {
                id       : 'list-certificates',
                title    : 'List certificates',
                type     : 'item',
                icon     : 'view_list',
                url      : '/pages/list-certificates',
            },
        ]
    },
];
