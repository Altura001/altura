#!/bin/bash
cd /home/kolade/projects/altura_nova/web
rm -rf .next
unset NEXT_PUBLIC_MEDUSA_PUBLISHABLE_KEY
npm run dev
